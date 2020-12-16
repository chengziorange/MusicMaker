package com.example.demo.util;

import com.example.demo.bean.AudioClip;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AudioMaker {
    // 测试时定义工作路径
    public static final String FFMPEG_PATH = "/opt/AudioMaker/ffmpeg";
    public static final String ORIGIN_AUDIO_DIR = "/opt/AudioMaker/origin/";
    private String workingDir;
    private String id;

    public AudioMaker() {
    }

    // 阻塞地处理，处理完后的文件名为 result.mp3
    public String combineAudioClips(List<AudioClip> clips) {
        List<String> delayedClipsName = new ArrayList<>();

        for (AudioClip clip : clips) {
            delayedClipsName.add(delayOneClip(clip));
        }

        String result = increaseVolumeOf(
                combineClips(delayedClipsName.size(), delayedClipsName.toArray(new String[]{})), 3);

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
    private String delayOneClip(AudioClip clip) {
        String outputFileName = clip.getTime() + "_" + clip.getName();

        String[] cmd = {
                FFMPEG_PATH,
                "-i",
                ORIGIN_AUDIO_DIR + clip.getName(),
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
                FFMPEG_PATH,
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
        cmdList.add(FFMPEG_PATH);
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
                System.err.println("Failed to execute ffmpeg command to combine clips. The return code is " + status);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return resultFileName;
    }

    // 返回生成的文件名
    private String increaseVolumeOf(String audioNames, int db) {
        String resultFileName = "result.mp3";

        String[] cmd = {
                FFMPEG_PATH,
                "-i",
                workingDir + audioNames,
                "-filter",
                "volume=volume=" + db + "dB",
                workingDir + "volume_up_audio.mp3",
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
                System.err.println("Failed to execute ffmpeg command to increase audio volume. The return code is " + status);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return resultFileName;
    }

    // 返回生成的文件名
    public String cutAudio(String name, int startTime, int endTime) {
        String output = "done_cutAudio.mp3";

        String[] cmd = {
                FFMPEG_PATH,
                "-i",
                workingDir + name,
                "-ss",
                startTime + "ms",
                "-t",
                endTime + "ms",
                "-acodec",
                "copy",
                workingDir + output,
                "-y"
        };

        try {
            Process process = Runtime.getRuntime().exec(cmd);

            // 阻塞当前线程，等待 ffmpeg 处理完毕
            int status = process.waitFor();
            if (status != 0) {
                System.err.println("Failed to execute ffmpeg command to cut one audio. The return code is " + status);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return output;
    }

    public void setRandomWorkingDir() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        id = sb.toString();
        workingDir = "/tmp/AudioMaker/" + id + "/";
        File file = new File(workingDir);
        file.mkdir();
    }

    public String getWorkingDir() {
        if (workingDir != null) {
            return workingDir;
        } else {
            System.err.println("Working Dir hasn't set.");
            return null;
        }
    }

    public String getWorkingDir(String id) {
        String result = "/tmp/AudioMaker/" + id + "/";
        File file = new File(result);
        if (file.exists()) {
            return "/tmp/AudioMaker/" + id + "/";
        } else {
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public String convertFileToBinStr() {
        File file = new File("/opt/AudioMaker/origin/Inception_chip3.mp3");

        try {
            InputStream fis = new FileInputStream(file);
            byte[] bytes = FileCopyUtils.copyToByteArray(fis);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new RuntimeException("transform file into bin String 出错", ex);
        }
    }
}