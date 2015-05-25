package idc.storyalbum.layout.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import idc.storyalbum.layout.model.template.PageTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yonatan on 15/5/2015.
 */
@Service
@Slf4j
public class TemplateReader {
    @Value("${story-album.template-dir}")
    private String templateDirName;

    @Autowired
    private ObjectMapper objectMapper;

    public Set<PageTemplate> readTemplates() {
        File templateDir = new File(templateDirName);
        log.info("Reading template dir {}", templateDir);

        Collection<File> files = FileUtils.listFiles(templateDir, new String[]{"json"}, false);
        Set<PageTemplate> templates = new HashSet<>();
        for (File file : files) {
            try {
                PageTemplate pageTemplate = objectMapper.readValue(file, PageTemplate.class);
                tryAddBackground(file, pageTemplate);
                templates.add(pageTemplate);
            } catch (Exception e) {
                log.warn("Error while parsing template {} - {}", file, e);
            }
        }
        log.info("Found {} templates", templates.size());
        return templates;
    }

    private void tryAddBackground(File file, PageTemplate pageTemplate) {
        if (pageTemplate.getBackgroundImageFile()!=null){
            return;
        }
        File backgroundFile = new File(FilenameUtils.removeExtension(file.getAbsolutePath()) + ".jpg");
        if (backgroundFile.isFile()) {
            try {
                BufferedImage background = ImageIO.read(backgroundFile);
                if (background != null && background.getWidth() == pageTemplate.getWidth() && background.getHeight() == pageTemplate.getHeight()) {
                    pageTemplate.setBackgroundImageFile(backgroundFile);
                    log.info("Found background for template {}", file);
                } else {
                    log.warn("Background image doesn't match {}", backgroundFile);
                }
            } catch (IOException e) {
                log.warn("Couldn't read background file {} - {}", backgroundFile, e);
            }
        }
    }

}
