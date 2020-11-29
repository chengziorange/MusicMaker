package com.example.demo.controller;

import com.example.demo.bean.AudioClip;
import com.example.demo.bean.CutTime;
import com.example.demo.util.AudioMaker;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MusicController {

    @PostMapping("/music/merge")
    public void mergeMusicClips(@RequestBody List<AudioClip> audioClips, HttpServletResponse response) {
        AudioMaker audioMaker = new AudioMaker();
        audioMaker.setRandomWorkingDir();
        String result = audioMaker.getWorkingDir() + audioMaker.combineAudioClips(audioClips);

        response.setContentType("application/json");
        try {
            PrintWriter writer = response.getWriter();
            Map<String, String> map = new HashMap<>();
            map.put("data", audioMaker.getId());
            map.put("status", "200");
            writer.write(map.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        File mergedAudio = new File(result);
//
//        try {
//            FileInputStream inputStream = new FileInputStream(mergedAudio);
//            byte[] data = new byte[(int) mergedAudio.length()];
//            inputStream.read(data);
//            inputStream.close();
//
//            response.setContentType("audio/mpeg");
//            response.addHeader("Content-Length", "" + data.length);
//
//            OutputStream outputStream = response.getOutputStream();
//            outputStream.write(data);
//            outputStream.flush();
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @GetMapping("/music/merge/{id}")
    public void getMergedMusic(@PathVariable(value = "id") String id, HttpServletResponse response) {
        AudioMaker audioMaker = new AudioMaker();
        String path = audioMaker.getWorkingDir(id) + "result.mp3";

        if (audioMaker.getWorkingDir(id) != null) {
            File mergedAudio = new File(path);

            try {
                FileInputStream inputStream = new FileInputStream(mergedAudio);
                byte[] data = new byte[(int) mergedAudio.length()];
                inputStream.read(data);
                inputStream.close();

                response.setContentType("audio/mpeg");
                response.addHeader("Content-Length", "" + data.length);

                OutputStream outputStream = response.getOutputStream();
                outputStream.write(data);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            response.setStatus(404);
        }
    }

    @PostMapping("/music/cut")
    public void cutMusic(MultipartHttpServletRequest request, HttpServletResponse response) {
        MultipartFile multipartFile = request.getFile("myfile");
        AudioMaker audioMaker = new AudioMaker();
        audioMaker.setRandomWorkingDir();
        File audioToCut = new File(audioMaker.getWorkingDir());
        try {
            multipartFile.transferTo(audioToCut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        CutTime cutTime = new CutTime();
        cutTime = gson.fromJson(request.getParameter("formData"), CutTime.class);
        String result = audioMaker.cutAudio(audioToCut.getPath(), cutTime.getStartTime(), cutTime.getEndTime());

        response.setContentType("application/json");
        try {
            PrintWriter writer = response.getWriter();
            Map<String, String> map = new HashMap<>();
            map.put("data", audioMaker.getId());
            map.put("status", "200");
            writer.write(map.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/music/cut/{id}")
    public void getCutAudio(@PathVariable(value = "id") String id, HttpServletResponse response) {
        AudioMaker audioMaker = new AudioMaker();
        String path = audioMaker.getWorkingDir(id) + "done_cutAudio.mp3";
        if (audioMaker.getWorkingDir(id) != null) {
            File cutAudio = new File(path);

            try {
                FileInputStream inputStream = new FileInputStream(cutAudio);
                byte[] data = new byte[(int) cutAudio.length()];
                inputStream.read(data);
                inputStream.close();

                response.setContentType("audio/mpeg");
                response.addHeader("Content-Length", "" + data.length);

                OutputStream outputStream = response.getOutputStream();
                outputStream.write(data);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            response.setStatus(404);
        }
    }
}
