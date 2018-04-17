package com.projectkr.shell.simple.shell;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.projectkr.shell.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by Hello on 2018/04/01.
 */

public class SimpleShellExecutor {
    private Context context;
    private boolean started = false;
    public SimpleShellExecutor(Context context) {
        this.context = context;
    }

    /**
     * 执行脚本
     * @param root
     * @param cmds
     * @param startPath
     */
    public boolean execute(Boolean root, StringBuilder cmds, String startPath) {
        if (started) {
            return false;
        }
        Process process = null;
        try {
            //process = Runtime.getRuntime().exec(root ? "su" : "bash");
            process = Runtime.getRuntime().exec(root ? "su" : "sh");
        } catch (Exception ex) {
            Toast.makeText(context, R.string.cannot_get_root_access, Toast.LENGTH_SHORT).show();
        }

        if (process != null) {
            TextView textView = setLogView();

            final ShellHandler shellHandler = new SimpleShellHandler(textView);
            setHandler(process, shellHandler);

            final OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            try {
                String start = startPath;
                if (startPath!=null) {
                    start = startPath;
                } else {
                    start = context.getFilesDir().getAbsolutePath();
                }
                dataOutputStream.write(String.format("cd '%s'\n", start).getBytes("UTF-8"));

                shellHandler.sendMessage(shellHandler.obtainMessage(ShellHandler.EVENT_START, "shell@android:" +start + " $\n\n"));
                shellHandler.sendMessage(shellHandler.obtainMessage(ShellHandler.EVENT_WRITE, cmds.toString()));

                dataOutputStream.writeBytes("sleep 0.2;\n");
                dataOutputStream.write(cmds.toString().getBytes("UTF-8"));
                dataOutputStream.writeBytes("\n\n");
                dataOutputStream.writeBytes("sleep 0.2;\n");
                dataOutputStream.writeBytes("exit\n");
                dataOutputStream.writeBytes("exit\n");
                dataOutputStream.flush();
            } catch (Exception ex) {
                process.destroy();
            }
            started = true;
        }
        return started;
    }

    /**
     * 创建并获取日志输出界面
     * @return
     */
    private TextView setLogView() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.dialog_shell_executor, null);
        TextView textView = (TextView) view.findViewById(R.id.shell_output);
        new AlertDialog.Builder(context)
                .setTitle(R.string.shell_executor)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {  }
                })
                .create()
                .show();
        return textView;
    }

    /**
     * 设置日志处理Handler
     * @param process Runtime进程
     * @param shellHandler ShellHandler
     */
    private void setHandler(Process process, final ShellHandler shellHandler) {
        final InputStream inputStream = process.getInputStream();
        final InputStream errorStream = process.getErrorStream();
        final Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                String line;
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    while ((line = bufferedReader.readLine()) != null) {
                        shellHandler.sendMessage(shellHandler.obtainMessage(ShellHandler.EVENT_REDE, line + "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        final Thread readerError = new Thread(new Runnable() {
            @Override
            public void run() {
                String line;
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream, "UTF-8"));
                    while ((line = bufferedReader.readLine()) != null) {
                        shellHandler.sendMessage(shellHandler.obtainMessage(ShellHandler.EVENT_READ_ERROR, line + "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        final Process processFinal = process;
        Thread waitExit = new Thread(new Runnable() {
            @Override
            public void run() {
                int status = -1;
                try {
                    status = processFinal.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    shellHandler.sendMessage(shellHandler.obtainMessage(ShellHandler.EVENT_EXIT, status));
                    if (reader.isAlive()) {
                        reader.interrupt();
                    }
                    if (readerError.isAlive()) {
                        readerError.interrupt();
                    }
                }
            }
        });

        reader.start();
        readerError.start();
        waitExit.start();
    }
}
