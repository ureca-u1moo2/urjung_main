package com.eureka.ip.team1.urjung_main.stt;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SttServiceImpl implements SttService {

    private final String openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Recognition";
    private final String accessKey = "dab3d0a2-d759-438b-a1f7-30399b283c00"; // 발급받은 API 키
    private final Gson gson = new Gson();

    @Override
    public String transcribeWav(MultipartFile file) {
        try {
            // 1. Base64로 인코딩
            byte[] audioBytes = file.getBytes();
            String audioContents = Base64.getEncoder().encodeToString(audioBytes);

            // 2. JSON 요청 구성
            Map<String, Object> request = new HashMap<>();
            Map<String, String> argument = new HashMap<>();
            argument.put("language_code", "korean");
            argument.put("audio", audioContents);
            request.put("argument", argument);

            // 3. HTTP 요청
            URL url = new URL(openApiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", accessKey);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(gson.toJson(request).getBytes("UTF-8"));
            wr.flush();
            wr.close();

            // 4. 응답 읽기
            InputStream is = con.getInputStream();
            String response = new String(is.readAllBytes());

            // 5. 응답에서 텍스트 추출
            Map<?, ?> responseMap = gson.fromJson(response, Map.class);
            Map<?, ?> returnObj = (Map<?, ?>) responseMap.get("return_object");
            String recognized = returnObj != null ? (String) returnObj.get("recognized") : null;

            return recognized != null ? recognized : "[인식 실패]";
        } catch (Exception e) {
            e.printStackTrace();
            return "[에러 발생: " + e.getMessage() + "]";
        }
    }
}
