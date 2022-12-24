package com.hzzz.points.utils.github_update_checker;

import com.alibaba.fastjson2.JSON;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>检查github上是否更新了</p>
 *
 * @author <a href="https://github.com/HowieHz/">HowieHz</a>
 * @version v0.2.4.1
 * @since 2022-12-24 23:06
 */
public class UpdateChecker {

    /**
     * 检查版本是否是最新
     *
     * @param current_version 当前版本
     * @return 不是最新就返回true
     */
    public boolean check(String current_version) {

        String url = "https://api.github.com/repos/HowieHz/Points/releases/latest";
        return compare(current_version, JSON.parseObject(hc2(url)).getString("tag_name").substring(1)) < 0;
    }

    /**
     * 获取json
     *
     * @param url 地址
     * @return 返回的为空就是获取失败
     */
    public String hc2(String url) {
        String response = "";
        BufferedReader in = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/18.17763";
            conn.setRequestProperty("User-Agent", USER_AGENT);

            if (conn.getResponseCode() == 200) {
                String line;
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = in.readLine()) != null) {
                    response += line;
                }
            }
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 语义化版本比较
     * 代码来自 <a href="https://blog.csdn.net/cainiao1412/article/details/107998031">...</a>
     *
     * @param version1 第一个版本
     * @param version2 第二个版本
     * @return 第一个小就-1，一样就0，第二个大就1
     */
    public static int compare(String version1, String version2) {
        final int MAX_LENGTH = 4;  // 语义化版本最大长度
        // 拆分
        String[] version1Arr = version1.split("\\.");
        String[] version2Arr = version2.split("\\.");
        for (int i = 0; i < MAX_LENGTH; i++) {
            // 数组长度不够 0 来填补,否则直接转换为int
            int v1 = version1Arr.length > i ? Integer.parseInt(version1Arr[i]) : 0;
            int v2 = version2Arr.length > i ? Integer.parseInt(version2Arr[i]) : 0;
            // 不相等则比较大小,分出结果
            if (v1 != v2) {
                return v1 < v2 ? -1 : 1;
            }
        }
        // 完全相等返回0
        return 0;
    }
}
