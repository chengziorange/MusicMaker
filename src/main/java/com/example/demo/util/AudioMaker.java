package com.example.demo.util;

import com.example.demo.bean.AudioClip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AudioMaker {
    // 测试时定义工作路径
    private final String ffmpegPath = "/tmp/AudioMaker/ffmpeg";
    private final String originAudioDir = "/tmp/AudioMaker/origin/";
    private final String workingDir = "/tmp/AudioMaker/test/";

    private static volatile AudioMaker instance;

    public static AudioMaker getInstance() {
        if (instance == null) {
            instance = new AudioMaker();
        }
        return instance;
    }

    private AudioMaker() {
    }

    // 阻塞地处理，处理完后的文件名为 result.mp3
    public String combineAudioClips(List<AudioClip> clips) {
        List<String> delayedClipsName = new ArrayList<>();

        for (AudioClip clip : clips) {
            delayedClipsName.add(delayOneClip(clip));
        }

        String result = combineClips(delayedClipsName.size(), delayedClipsName.toArray(new String[]{}));

        // 两个两个拼接的旧方法
//        if (delayedClipsName.size() >= 2) {
//            for (int i = 1; i < delayedClipsName.size(); i++) {
//                if (i == 1) {
//                    combineTwoClips(delayedClipsName.get(1), delayedClipsName.get(0));
//                } else {
//                    combineTwoClips(delayedClipsName.get(i), "combined_two_audio.mp3");
//                }
//            }
//        }

        File oldName = new File(workingDir + "/" + "combined_two_audio.mp3");
        File newName = new File(workingDir + "/" + "result.mp3");
        oldName.renameTo(newName);

        return result;
    }

    // 返回延迟处理后的音频文件名
    public String delayOneClip(AudioClip clip) {
        String outputFileName = clip.getTime() + "_" + clip.getName();

        String[] cmd = {
                ffmpegPath,
                "-i",
                originAudioDir + clip.getName(),
                "-filter_complex",
                "adelay=delays=" + clip.getTime() + ":all=1",
                workingDir + outputFileName,
                "-y"
        };

        try {
            Process process = Runtime.getRuntime().exec(cmd);

            // 读出标准输出和错误
//            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line = "";
//            while ((line = input.readLine()) != null) {
//                System.out.println(line);
//            }
//            BufferedReader errInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            String errLine = "";
//            while ((errLine = input.readLine()) != null) {
//                System.out.println(errLine);
//            }

            // 阻塞当前线程，等待 ffmpeg 处理完毕
            int status = process.waitFor();
            if (status != 0) {
                System.err.println("Failed to execute ffmpeg command to delay one clip. The return code is " + status);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return outputFileName;
    }

    // 两个两个拼接的旧方法
    private void combineTwoClips(String clipName1, String clipName2) {
        String[] cmd = {
                ffmpegPath,
                "-i",
                workingDir + clipName1,
                "-i",
                workingDir + clipName2,
                "-filter_complex",
                "amix=dropout_transition=0",
                workingDir + "combined_two_audio.mp3",
                "-y"
        };

        // 测试拼接后的字符串
//        StringBuilder stringBuilder = new StringBuilder();
//        for (String string : cmd
//        ) {
//            stringBuilder.append(string + " ");
//        }
//        String str = stringBuilder.toString();

        try {
            Process process = Runtime.getRuntime().exec(cmd);
            // 阻塞当前线程，等待 ffmpeg 处理完毕
            int status = process.waitFor();
            if (status != 0) {
                System.err.println("Failed to execute ffmpeg command to combine two clips. The return code is " + status);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 返回生成的文件名
    private String combineClips(int numbers, String... clipNames) {
        String resultFileName = "result.mp3";

        List<String> cmdList = new ArrayList<>();
        cmdList.add(ffmpegPath);
        for (String name : clipNames) {
            cmdList.add("-i");
            cmdList.add(workingDir + name);
        }
        cmdList.addAll(Arrays.asList("-filter_complex",
                "amix=inputs=" + numbers + ":dropout_transition=0",
                workingDir + resultFileName,
                "-y"));
        String[] cmd = cmdList.toArray(new String[]{});

        // 测试拼接后的字符串
//        StringBuilder stringBuilder = new StringBuilder();
//        for (String string : cmd
//        ) {
//            stringBuilder.append(string + " ");
//        }
//        String str = stringBuilder.toString();

        try {
            Process process = Runtime.getRuntime().exec(cmd);
            // 阻塞当前线程，等待 ffmpeg 处理完毕
            int status = process.waitFor();
            if (status != 0) {
                System.err.println("Failed to execute ffmpeg command to combine two clips. The return code is " + status);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return resultFileName;
    }
}