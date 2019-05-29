package com.omarea.krscript.config;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionParamInfo {
    // 参数名：必需保持唯一
    public String name;

    // 描述
    public String desc;

    // 值
    public String value;
    public String valueShell;
    public String valueFromShell;
    public int maxLength = -1;
    public String type;
    public boolean readonly;
    public ArrayList<ActionParamOption> options;
    public String optionsSh = "";

    public static class ParamInfoFilter implements InputFilter {
        private ActionParamInfo paramInfo;

        public ParamInfoFilter(ActionParamInfo paramInfo) {
            this.paramInfo = paramInfo;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source != null && source.toString().contains("\"")) {
                return "";
            }

            if (paramInfo.maxLength >= 0) {
                int keep = paramInfo.maxLength - (dest.length() - (dend - dstart));
                if (keep <= 0) {
                    // 如果超出字数限制，就返回“”
                    return "";
                }
            }

            if (paramInfo.type != null && !paramInfo.type.equals("") && source != null) {
                if (paramInfo.type.equals("int")) {
                    Pattern regex = Pattern.compile("^[0-9]{0,}$");
                    Matcher matcher = regex.matcher(source.toString());
                    if (!matcher.matches()) {
                        return "";
                    }
                } else if (paramInfo.type.equals("number")) {
                    Pattern regex = Pattern.compile("^[\\-.,0-9]{0,}$");
                    Matcher matcher = regex.matcher(source.toString());
                    if (!matcher.matches()) {
                        return "";
                    }
                }
            }
            return null;
        }
    }

    public static class ActionParamOption {
        public String value;
        public String desc;
    }
}
