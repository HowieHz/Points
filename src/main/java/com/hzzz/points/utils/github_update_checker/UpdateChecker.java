package com.hzzz.points.utils.github_update_checker;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hzzz.points.utils.data_structure.tuple.Tuple4;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
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
     * 工具类禁止实例化
     */
    private UpdateChecker() {
        throw new IllegalStateException("工具类");
    }

    /**
     * 检查版本是否是最新
     *
     * @param current_version 当前版本，如v1.0
     * @return (第一个值为true则为获取信息成功 ， 第二个值为true为需要更新 ， 最新版本版本号 ， 最新版本的releases界面)
     */
    @Contract("_ -> new")
    static public @NotNull Tuple4<Boolean, Boolean, String, String> check(String current_version) {
        //TODO 自己进行json解析
        String url = "https://api.github.com/repos/HowieHz/Points/releases/latest";
        JSONObject obj = JSON.parseObject(getJson(url));

        int response_code = obj.getIntValue("response_code", 200);
        if (response_code != 200) {
            return new Tuple4<>(false, false, "", "");
        }

        // String downloadUrl = obj.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
        String latest_version = obj.getString("tag_name");
        return new Tuple4<>(true,
                compare(current_version, latest_version.substring(1)) < 0,
                latest_version,
                obj.getString("html_url"));
    }

    /**
     * 获取json
     *
     * @param url 地址
     * @return 返回的为空就是获取失败
     */
    static private @NotNull String getJson(String url) {
        StringBuilder response = new StringBuilder();
        BufferedReader in = null;
        int responseCode = 0;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/18.17763";
            conn.setRequestProperty("User-Agent", USER_AGENT);
            responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                return "{\"response_code\":" + responseCode + "}";
            }

            String line;
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "{\"response_code\":" + responseCode + "}";
    }

    /**
     * 语义化版本比较
     * 代码来自 <a href="https://blog.csdn.net/cainiao1412/article/details/107998031">...</a>
     *
     * @param version1 第一个版本
     * @param version2 第二个版本
     * @return 第一个小就-1，一样就0，第二个大就1
     */
    static private int compare(@NotNull String version1, @NotNull String version2) {
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
