package com.example.demo.controller;

import com.example.demo.bean.AudioClip;
import com.example.demo.bean.CutTime;
import com.example.demo.util.AudioMaker;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MusicController {

    @PostMapping("/music/merge")
    public void mergeMusicClips(@RequestBody List<AudioClip> audioClips, HttpServletResponse response) {
        if (audioClips == null || audioClips.isEmpty()) {
            response.setStatus(404);
            return;
        }

        AudioMaker audioMaker = new AudioMaker();
        audioMaker.setRandomWorkingDir();
        String result = audioMaker.getWorkingDir() + audioMaker.combineAudioClips(audioClips);

        response.setContentType("application/json");
        try {
            PrintWriter writer = response.getWriter();
            Map<String, String> map = new HashMap<>();
            map.put("data", audioMaker.getId());
            map.put("statusCode", "200");
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
    public void cutMusic(
            //HttpServletRequest request,
            @RequestParam(value = "myfile") MultipartFile[] files,
            @RequestParam("startTime") int startTime,
            @RequestParam("endTime") int endTime,
            HttpServletResponse response) {

        // LOG
        // System.out.println(request.toString());

        // MultipartHttpServletRequest params = (MultipartHttpServletRequest) request;

        // MultipartFile multipartFile = params.getFile("name");
        MultipartFile multipartFile = files[0];
        AudioMaker audioMaker = new AudioMaker();
        audioMaker.setRandomWorkingDir();
        File audioToCut = new File(audioMaker.getWorkingDir() + "todo_cutAudio.mp3");
        BufferedOutputStream outputStream = null;
        if (!multipartFile.isEmpty()) {
            try {
                byte[] bytes = multipartFile.getBytes();
                outputStream = new BufferedOutputStream(new FileOutputStream(
                        audioToCut));
                outputStream.write(bytes);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Gson gson = new Gson();
        // CutTime cutTime = new CutTime();
        // cutTime = gson.fromJson(cutTimeStr, CutTime.class);
        // String result = audioMaker.cutAudio("todo_cutAudio.mp3", cutTime.getStartTime(), cutTime.getEndTime());
        String result = audioMaker.cutAudio("todo_cutAudio.mp3", startTime, endTime);

        response.setContentType("application/json");
        try {
            PrintWriter writer = response.getWriter();
            Map<String, String> map = new HashMap<>();
            map.put("data", audioMaker.getId());
            map.put("statusCode", "200");
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

    @GetMapping("/music/test")
    public String convert() {
        AudioMaker audioMaker = new AudioMaker();
        String str = audioMaker.convertFileToBinStr();
        System.out.println(str);
        return str;
    }
}
