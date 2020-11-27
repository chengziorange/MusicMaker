package com.example.demo.controller;

import com.example.demo.bean.AudioClip;
import com.example.demo.util.AudioMaker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
public class MusicController {

    @PostMapping("/music/merge")
    public void mergeMusicClips(@RequestBody List<AudioClip> audioClips, HttpServletResponse response) {
        AudioMaker audioMaker = AudioMaker.getInstance();
        String result = "/tmp/AudioMaker/test/" + audioMaker.combineAudioClips(audioClips);

        File mergedAudio = new File(result);

        try {
            FileInputStream inputStream = new FileInputStream(mergedAudio);
            byte[] data = new byte[(int) mergedAudio.length()];
            inputStream.read(data);
            inputStream.close();

            String fileName = URLEncoder.encode("文件流形式视频.mp4", StandardCharsets.UTF_8);

            response.setContentType("audio/mpeg");
            response.addHeader("Content-Length", "" + data.length);

            OutputStream outputStream = response.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
