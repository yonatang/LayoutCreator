package idc.storyalbum.layout.service.searcher;

import idc.storyalbum.layout.model.layout.ImageFrame;
import idc.storyalbum.layout.model.layout.Layout;
import idc.storyalbum.layout.model.layout.PageLayout;
import idc.storyalbum.layout.model.template.Frame;
import idc.storyalbum.layout.model.template.PageTemplate;
import idc.storyalbum.layout.service.ImageService;
import idc.storyalbum.model.album.Album;
import idc.storyalbum.model.album.AlbumPage;
import idc.storyalbum.model.image.Rectangle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

/**
 * Created by yonatan on 16/5/2015.
 */
@Slf4j
public abstract class AbsSearcher {

    @Autowired
    private ImageService imageService;

    abstract Layout searchLayoutImpl(Album album, Map<Integer, Set<PageTemplate>> templatesBySize, int maxPageLayouts);

    public Layout searchLayout(Album album, Set<PageTemplate> templates, int maxPageLayouts) {
        log.info("Searching for album with {} picutres using {} templates", album.getPages().size(), templates.size());
        if (maxPageLayouts > 0) {
            log.info("Limited to {} pages in the output layout", maxPageLayouts);
        }
        Map<Integer, Set<PageTemplate>> templatesBySize
                = templates.stream().collect(groupingBy(x -> x.getFrames().size(), toSet()));
        if (templatesBySize.containsKey(0)) {
            log.warn("Template(s) with zero images found. Not using it.");
            templatesBySize.remove(0);
        }

        Layout best = searchLayoutImpl(album, templatesBySize, maxPageLayouts);
        return best;
    }


    private void incArr(int[] idxes_max, int[] idxes) {
        for (int i = 0; i < idxes.length; i++) {
            idxes[i]++;
            if (idxes[i] == idxes_max[i]) {
                idxes[i] = 0;
            } else {
                break;
            }
        }
    }

    Layout findBestMatchingLayout(Map<Integer, Set<PageTemplate>> templatesBySize,
                                  Album album,
                                  List<Integer> layoutOption) {
        List<List<PageTemplate>> allOptions = new ArrayList<>();
        int totalOptions = 1;
        int[] idxes = new int[layoutOption.size()];
        int[] idxes_max = new int[layoutOption.size()];
        for (int i = 0; i < layoutOption.size(); i++) {
            int size = layoutOption.get(i);
            allOptions.add(new ArrayList<>(templatesBySize.get(size)));
            totalOptions *= templatesBySize.get(size).size();
            idxes_max[i] = templatesBySize.get(size).size();
        }

        int i = 0;
        int totalPages = album.getPages().size();
        double bestScore = Double.NEGATIVE_INFINITY;
        Layout bestLayout = null;
        while (i < totalOptions) {
            i++;
            if (log.isDebugEnabled() && (i % 10000 == 0)) {
                log.debug("    Looking at option {}/{}", i, totalOptions);
            }
            Layout layout = new Layout();
            int pageIdx = 0;
            double score = 0;
            int maxn = 0;
            for (int j = 0; j < idxes.length; j++) {
                PageTemplate pageTemplate = allOptions.get(j).get(idxes[j]);
                PageLayout pageLayout = createPageLayout(pageTemplate, album, pageIdx);
                maxn = Math.max(maxn, pageLayout.getImageFrames().size());
                pageIdx += pageLayout.getImageFrames().size();
                score += calcPageLayoutScore(totalPages, pageLayout);
                layout.getPages().add(pageLayout);
                pageLayout.setScore(score);
            }
            score += 0.1 * (totalPages * totalPages / (maxn * maxn));
            layout.setScore(score);
            if (score > bestScore) {
                bestLayout = layout;
                bestScore = score;
            }

            incArr(idxes_max, idxes);

        }
//        int pageIdx = 0;
//        Layout layout = new Layout();
//        for (Integer size : layoutOption) {
//            PageLayout pageLayout = findBestMatchingPageTemplate(album, templatesBySize, size, pageIdx);
//            layout.getPages().add(pageLayout);
//            pageIdx += size;
//        }
        log.debug("Found candidate layout {}",bestLayout);
        return bestLayout;
    }

    private double calcPageLayoutScore(int totalPages, PageLayout pageTemplate) {
        double score = 0;
        for (ImageFrame imageFrame : pageTemplate.getImageFrames()) {
            double textScore = imageFrame.getTextScore();
            double fitScore = imageFrame.getFitScore();
            double orientationScore = imageFrame.getOrientationScore();
            score += (0.5 / totalPages) * fitScore + (0.2 / totalPages) * textScore + (0.2 / totalPages) * orientationScore;
        }
        return score;
    }

    private PageLayout findBestMatchingPageTemplate(Album album, Map<Integer, Set<PageTemplate>> templatesBySize,
                                                    Integer size, int pageIdx) {
        log.debug("  Searching for pages of size {}", size);
        Set<PageTemplate> pageTemplates = templatesBySize.get(size);
        PageLayout best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        int totalPages = album.getPages().size();
        for (PageTemplate pageTemplate : pageTemplates) {
            PageLayout candidate = createPageLayout(pageTemplate, album, pageIdx);
            double score = calcPageLayoutScore(totalPages, candidate);
            if (score > bestScore) {
                best = candidate;
                bestScore = score;
            }
        }
        return best;
    }

    private PageLayout createPageLayout(PageTemplate pageTemplate, Album album, int pageIdx) {
//        log.debug("  Page Template {}", pageTemplate);
        int templateSize = pageTemplate.getFrames().size();
        List<AlbumPage> subAlbumPages = album.getPages().subList(pageIdx, pageIdx + templateSize);
        PageLayout pageLayout = new PageLayout();
        pageLayout.setHeight(pageTemplate.getHeight());
        pageLayout.setWidth(pageTemplate.getWidth());
        double score = 0;
        for (int i = 0; i < templateSize; i++) {
            AlbumPage albumPage = subAlbumPages.get(i);
            Frame templateFrame = pageTemplate.getFrames().get(i);
//            log.debug("    Trying templateFrame {}", templateFrame);
            ImageFrame imageFrame = new ImageFrame();
            pageLayout.getImageFrames().add(imageFrame);
            imageFrame.setImageRect(new Rectangle(templateFrame.getImageRect()));
            imageFrame.setTextRect(new Rectangle(templateFrame.getTextRect()));
            imageFrame.setText(albumPage.getText());


            BufferedImage bufferedImage = imageService.loadImage(album.getBaseDir(), albumPage.getImage().getImageFilename());
//            log.debug("    Image {}x{}", bufferedImage.getWidth(), bufferedImage.getHeight());
            Pair<BufferedImage, Double> croppedImageData =
                    imageService.cropImage(albumPage.getImage(), bufferedImage, imageFrame.getImageRect().getDimension());
            imageFrame.setImage(croppedImageData.getLeft());

            imageFrame.setFitScore(croppedImageData.getRight());
            imageFrame.setOrientationScore(
                    imageService.getOrientation(bufferedImage) == imageService.getOrientation(imageFrame.getImageRect()) ? 1 : 0);

            ImageService.TextImageHolder textImageHolder =
                    imageService.getTextImage(albumPage.getText(), pageTemplate, imageFrame.getTextRect());
            imageFrame.setTextImage(textImageHolder.getImage());
            imageFrame.setTextScore(calcTextScore(textImageHolder));
        }
        return pageLayout;
    }

    private double calcTextScore(ImageService.TextImageHolder imageHolder) {
        if (imageHolder.getActualFontSize() == imageHolder.getInitFontSize()) {
            double r = (double) imageHolder.getTextHeight() / (double) imageHolder.getFrameHeight();
            if (r > 0.33333) {
                return 1.0;
            } else {
                return r * 1.5;
            }
        } else {
            return 1.0 - (double) (imageHolder.getActualFontSize() - 10) / (double) (imageHolder.getInitFontSize() - 10);
        }
    }


}
