### oauth2
![](https://img.shields.io/badge/language-java-orange.svg) ![](https://img.shields.io/badge/qq-923260818-brightgreen.svg) ![](https://img.shields.io/badge/build-%20passing-brightgreen.svg) ![](https://img.shields.io/badge/created-December-brightgreen.svg)

#### 概述
微服务下oauth2鉴权demo,整合spring cloud zuul、eureka、spring security、oauth2、jwt、spring boot

#### 示例说明

1. 登陆获取token

    参数名称          | 类型     | 参数说明             | 示例值        
    ------------- | ------ | ---------------- | ---------- 
    client_id     | String | 客户端id            | abcdid     
    client_secret | String | 客户端秘钥            | abcdsecret 
    username      | String | 用户名              | uuser      
    password      | String | 用户密码             | pword      
    grant_type    | String | 授权模式,固定为password | password   

   注：header中Authorization 值为Basic + base64串（client_id:client_secret）

   请求示例:

   HTTP:

   ```
   POST /login/oauth/token HTTP/1.1
   Host: 192.168.13.114:8081
   Authorization: Basic ZnVjazpmdWNr
   Content-Type: application/x-www-form-urlencoded

   username=ufuck&password=pfuck&grant_type=password
   ```

   CURL:

   ```
   curl -X POST \
     http://192.168.13.114:8081/login/oauth/token \
     -H 'Authorization: Basic ZnVjazpmdWNr' \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -d 'username=ufuck&password=pfuck&grant_type=password'
   ```

   Java:

   ```
   OkHttpClient client = new OkHttpClient().newBuilder()
                   .connectTimeout(3, TimeUnit.SECONDS)
                   .readTimeout(5, TimeUnit.SECONDS)
                   .writeTimeout(5, TimeUnit.SECONDS)
                   .build();
           MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
           RequestBody body = RequestBody.create(mediaType, "username=ufuck&password=pfuck&grant_type=password");
           Request request = new Request.Builder()
                   .url("http://192.168.13.114:8081/login/oauth/token")
                   .post(body)
                   .addHeader("Authorization", "Basic ZnVjazpmdWNr")
                   .addHeader("Content-Type", "application/x-www-form-urlencoded")
                   .build();
           try (Response response = client.newCall(request).execute()) {
               System.out.println(response.body().string());
           } catch (Exception e) {

           }
   ```

   Python:

   ```
   import requests

   url = "http://192.168.13.114:8081/login/oauth/token"

   payload = "username=ufuck&password=pfuck&grant_type=password"
   headers = {
       'Authorization': "Basic ZnVjazpmdWNr",
       'Content-Type': "application/x-www-form-urlencoded"
       }

   response = requests.request("POST", url, data=payload, headers=headers)

   print(response.text)
   ```

   Jquery

   ```
   var settings = {
     "async": true,
     "crossDomain": true,
     "url": "http://192.168.13.114:8081/login/oauth/token",
     "method": "POST",
     "headers": {
       "Authorization": "Basic ZnVjazpmdWNr",
       "Content-Type": "application/x-www-form-urlencoded"
     },
     "data": {
       "username": "ufuck",
       "password": "pfuck",
       "grant_type": "password"
     }
   }

   $.ajax(settings).done(function (response) {
     console.log(response);
   });
   ```

   Nodejs:

   ```
   var request = require("request");

   var options = { method: 'POST',
     url: 'http://192.168.13.114:8081/login/oauth/token',
     headers: 
      { 'Content-Type': 'application/x-www-form-urlencoded',
        Authorization: 'Basic ZnVjazpmdWNr' },
     form: { username: 'ufuck', password: 'pfuck', grant_type: 'password' } };

   request(options, function (error, response, body) {
     if (error) throw new Error(error);

     console.log(body);
   });

   ```

   返回值示例:

   ```
   {
       "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTQzMTE0ODIsInVzZXJJZCI6InVmdWNrLXVpZCIsInVzZXJfbmFtZSI6InVmdWNrIiwianRpIjoiOTFkMzA3MGQtN2U5Yi00ZGUxLWE4MTEtZGM3NTNkNTFlN2FjIiwiY2xpZW50X2lkIjoiZnVjayIsInNjb3BlIjpbImFsbCJdfQ.7UPkjMPFIIAY_bApYch2yGt14wobe-Rof-jigp1LzUI",
       "token_type": "bearer",
       "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1ZnVjayIsInNjb3BlIjpbImFsbCJdLCJhdGkiOiI5MWQzMDcwZC03ZTliLTRkZTEtYTgxMS1kYzc1M2Q1MWU3YWMiLCJleHAiOjE1MTY3ODA4ODUsInVzZXJJZCI6InVmdWNrLXVpZCIsImp0aSI6IjYwODczODA0LTYwZDYtNDA4MC04NGJkLTYwZmY3MDQ5MTE1NyIsImNsaWVudF9pZCI6ImZ1Y2sifQ.rGxqvI84X5ZBkQFhF0fXRTYU76BMjeOVKARiq9-yp-8",
       "expires_in": 43199,
       "scope": "all",
       "userId": "ufuck-uid",
       "jti": "91d3070d-7e9b-4de1-a811-dc753d51e7ac"
   }
   ```

2. 刷新token

    参数名称          | 类型     | 参数说明                            | 示例值           
    ------------- | ------ | ------------------------------- | ------------- 
    client_id     | String | 客户端id                           | abcdid        
    client_secret | String | 客户端秘钥                           | abcdsecret    
    refresh_token | String | 用于刷新Access Token用的Refresh Token | uuser         
    grant_type    | String | 操作类型,固定为refresh_token           | refresh_token 

   注：header中Authorization 值为Basic + base64串（client_id:client_secret）

   请求示例:

   HTTP:

   ```
   POST /login/oauth/token HTTP/1.1
   Host: 192.168.13.114:8081
   Authorization: Basic ZnVjazpmdWNr
   Content-Type: application/x-www-form-urlencoded

   refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1ZnVjayIsInNjb3BlIjpbImFsbCJdLCJhdGkiOiI5YzFiZjljNS1iNWMwLTQ2MDItODNkMC1jODJhNjI3Zjk5MTEiLCJleHAiOjE1MTY3ODA4ODUsInVzZXJJZCI6InVmdWNrLXVpZCIsImp0aSI6IjYwODczODA0LTYwZDYtNDA4MC04NGJkLTYwZmY3MDQ5MTE1NyIsImNsaWVudF9pZCI6ImZ1Y2sifQ.VrUK1Pw6N33Pj9zMdR2_7T1ywizB1IblFQ0gKan0kIo&grant_type=refresh_token
   ```

   CURL:

   ```
   curl -X POST \
     http://192.168.13.114:8081/login/oauth/token \
     -H 'Authorization: Basic ZnVjazpmdWNr' \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -d 'refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1ZnVjayIsInNjb3BlIjpbImFsbCJdLCJhdGkiOiI5YzFiZjljNS1iNWMwLTQ2MDItODNkMC1jODJhNjI3Zjk5MTEiLCJleHAiOjE1MTY3ODA4ODUsInVzZXJJZCI6InVmdWNrLXVpZCIsImp0aSI6IjYwODczODA0LTYwZDYtNDA4MC04NGJkLTYwZmY3MDQ5MTE1NyIsImNsaWVudF9pZCI6ImZ1Y2sifQ.VrUK1Pw6N33Pj9zMdR2_7T1ywizB1IblFQ0gKan0kIo&grant_type=refresh_token'
   ```

   Java:

   ```
   OkHttpClient client = new OkHttpClient().newBuilder()
                   .connectTimeout(3, TimeUnit.SECONDS)
                   .readTimeout(5, TimeUnit.SECONDS)
                   .writeTimeout(5, TimeUnit.SECONDS)
                   .build();
   MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
   RequestBody body = RequestBody.create(mediaType, "refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1ZnVjayIsInNjb3BlIjpbImFsbCJdLCJhdGkiOiI5YzFiZjljNS1iNWMwLTQ2MDItODNkMC1jODJhNjI3Zjk5MTEiLCJleHAiOjE1MTY3ODA4ODUsInVzZXJJZCI6InVmdWNrLXVpZCIsImp0aSI6IjYwODczODA0LTYwZDYtNDA4MC04NGJkLTYwZmY3MDQ5MTE1NyIsImNsaWVudF9pZCI6ImZ1Y2sifQ.VrUK1Pw6N33Pj9zMdR2_7T1ywizB1IblFQ0gKan0kIo&grant_type=refresh_token");
   Request request = new Request.Builder()
     .url("http://192.168.13.114:8081/login/oauth/token")
     .post(body)
     .addHeader("Authorization", "Basic ZnVjazpmdWNr")
     .addHeader("Content-Type", "application/x-www-form-urlencoded")
     .build();
   try (Response response = client.newCall(request).execute()) {
       System.out.println(response.body().string());
   } catch (Exception e) {

   }
   ```

   Python:

   ```
   import requests

   url = "http://192.168.13.114:8081/login/oauth/token"

   payload = "refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1ZnVjayIsInNjb3BlIjpbImFsbCJdLCJhdGkiOiI5YzFiZjljNS1iNWMwLTQ2MDItODNkMC1jODJhNjI3Zjk5MTEiLCJleHAiOjE1MTY3ODA4ODUsInVzZXJJZCI6InVmdWNrLXVpZCIsImp0aSI6IjYwODczODA0LTYwZDYtNDA4MC04NGJkLTYwZmY3MDQ5MTE1NyIsImNsaWVudF9pZCI6ImZ1Y2sifQ.VrUK1Pw6N33Pj9zMdR2_7T1ywizB1IblFQ0gKan0kIo&grant_type=refresh_token"
   headers = {
       'Authorization': "Basic ZnVjazpmdWNr",
       'Content-Type': "application/x-www-form-urlencoded"
       }

   response = requests.request("POST", url, data=payload, headers=headers)

   print(response.text)
   ```

   Jquery:

   ```
   var settings = {
     "async": true,
     "crossDomain": true,
     "url": "http://192.168.13.114:8081/login/oauth/token",
     "method": "POST",
     "headers": {
       "Authorization": "Basic ZnVjazpmdWNr",
       "Content-Type": "application/x-www-form-urlencoded"
     },
     "data": {
       "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1ZnVjayIsInNjb3BlIjpbImFsbCJdLCJhdGkiOiI5YzFiZjljNS1iNWMwLTQ2MDItODNkMC1jODJhNjI3Zjk5MTEiLCJleHAiOjE1MTY3ODA4ODUsInVzZXJJZCI6InVmdWNrLXVpZCIsImp0aSI6IjYwODczODA0LTYwZDYtNDA4MC04NGJkLTYwZmY3MDQ5MTE1NyIsImNsaWVudF9pZCI6ImZ1Y2sifQ.VrUK1Pw6N33Pj9zMdR2_7T1ywizB1IblFQ0gKan0kIo",
       "grant_type": "refresh_token"
     }
   }

   $.ajax(settings).done(function (response) {
     console.log(response);
   });
   ```

   Nodejs:

   ```
   var request = require("request");

   var options = { method: 'POST',
     url: 'http://192.168.13.114:8081/login/oauth/token',
     headers: 
      { 'Content-Type': 'application/x-www-form-urlencoded',
        Authorization: 'Basic ZnVjazpmdWNr' },
     form: 
      { refresh_token: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1ZnVjayIsInNjb3BlIjpbImFsbCJdLCJhdGkiOiI5YzFiZjljNS1iNWMwLTQ2MDItODNkMC1jODJhNjI3Zjk5MTEiLCJleHAiOjE1MTY3ODA4ODUsInVzZXJJZCI6InVmdWNrLXVpZCIsImp0aSI6IjYwODczODA0LTYwZDYtNDA4MC04NGJkLTYwZmY3MDQ5MTE1NyIsImNsaWVudF9pZCI6ImZ1Y2sifQ.VrUK1Pw6N33Pj9zMdR2_7T1ywizB1IblFQ0gKan0kIo',
        grant_type: 'refresh_token' } };

   request(options, function (error, response, body) {
     if (error) throw new Error(error);

     console.log(body);
   });

   ```

   返回值示例:

   ```
   {
       "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTQzOTg3MzEsInVzZXJJZCI6InVmdWNrLXVpZCIsInVzZXJfbmFtZSI6InVmdWNrIiwianRpIjoiYjI5ZDU2MDYtYmQwMC00NDExLWI2NjMtZmIyZmJmYzg2YzA4IiwiY2xpZW50X2lkIjoiZnVjayIsInNjb3BlIjpbImFsbCJdfQ.Lk0dYTha50SCq1TtAlHFcmCMUEAfbNl3g1epyLTQJJo",
       "token_type": "bearer",
       "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1ZnVjayIsInNjb3BlIjpbImFsbCJdLCJhdGkiOiJiMjlkNTYwNi1iZDAwLTQ0MTEtYjY2My1mYjJmYmZjODZjMDgiLCJleHAiOjE1MTY3ODA4ODUsInVzZXJJZCI6InVmdWNrLXVpZCIsImp0aSI6IjYwODczODA0LTYwZDYtNDA4MC04NGJkLTYwZmY3MDQ5MTE1NyIsImNsaWVudF9pZCI6ImZ1Y2sifQ.KH6O5JZtxSVuYBPa9FIgVgTNzDsvHZ1TJ283sasE9Ts",
       "expires_in": 43199,
       "scope": "all",
       "userId": "ufuck-uid",
       "jti": "b29d5606-bd00-4411-b663-fb2fbfc86c08"
   }
   ```

3. 请求后端服务接口

    参数名称         | 类型     | 参数说明         | 示例值                                      
    ------------ | ------ | ------------ | ---------------------------------------- 
    access_token | String | access_token | eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTQzMDEzODUsInVzZXJJZCI6InVzZXIxLXVpZCIsInVzZXJfbmFtZSI6InVzZXIxIiwianRpIjoiMjE3ZjgzNGUtMTVjZi00YzUyLTkwZGMtNjU2OGRhZDIzYmNmIiwiY2xpZW50X2lkIjoiZnVjayIsInNjb3BlIjpbImFsbCJdfQ.thfwWjkIWZ0beYGfSwkaX7H1uUpwI2YrLEa-WWt7a90 

   请求示例:

   HTTP:

   ```
   POST /demo/test2 HTTP/1.1
   Host: localhost:8081
   Authorization: bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTQzOTg3MzEsInVzZXJJZCI6InVmdWNrLXVpZCIsInVzZXJfbmFtZSI6InVmdWNrIiwianRpIjoiYjI5ZDU2MDYtYmQwMC00NDExLWI2NjMtZmIyZmJmYzg2YzA4IiwiY2xpZW50X2lkIjoiZnVjayIsInNjb3BlIjpbImFsbCJdfQ.Lk0dYTha50SCq1TtAlHFcmCMUEAfbNl3g1epyLTQJJo
   Content-Type: application/x-www-form-urlencoded
   ```

   CURL:

   ```
   curl -X POST \
     http://localhost:8081/demo/test2 \
     -H 'Authorization: bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTQzOTg3MzEsInVzZXJJZCI6InVmdWNrLXVpZCIsInVzZXJfbmFtZSI6InVmdWNrIiwianRpIjoiYjI5ZDU2MDYtYmQwMC00NDExLWI2NjMtZmIyZmJmYzg2YzA4IiwiY2xpZW50X2lkIjoiZnVjayIsInNjb3BlIjpbImFsbCJdfQ.Lk0dYTha50SCq1TtAlHFcmCMUEAfbNl3g1epyLTQJJo' \
     -H 'Content-Type: application/x-www-form-urlencoded' 
   ```

   Java:

   ```
   OkHttpClient client = new OkHttpClient().newBuilder()
                   .connectTimeout(3, TimeUnit.SECONDS)
                   .readTimeout(5, TimeUnit.SECONDS)
                   .writeTimeout(5, TimeUnit.SECONDS)
                   .build();
   Request request = new Request.Builder()
     .url("http://localhost:8081/demo/test2")
     .post(null)
     .addHeader("Authorization", "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTQzOTg3MzEsInVzZXJJZCI6InVmdWNrLXVpZCIsInVzZXJfbmFtZSI6InVmdWNrIiwianRpIjoiYjI5ZDU2MDYtYmQwMC00NDExLWI2NjMtZmIyZmJmYzg2YzA4IiwiY2xpZW50X2lkIjoiZnVjayIsInNjb3BlIjpbImFsbCJdfQ.Lk0dYTha50SCq1TtAlHFcmCMUEAfbNl3g1epyLTQJJo")
     .addHeader("Content-Type", "application/x-www-form-urlencoded")
     .build();
   try (Response response = client.newCall(request).execute()) {
       System.out.println(response.body().string());
   } catch (Exception e) {

   }
   ```

   Python:

   ```
   import requests

   url = "http://localhost:8081/demo/test2"

   headers = {
       'Authorization': "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTQzOTg3MzEsInVzZXJJZCI6InVmdWNrLXVpZCIsInVzZXJfbmFtZSI6InVmdWNrIiwianRpIjoiYjI5ZDU2MDYtYmQwMC00NDExLWI2NjMtZmIyZmJmYzg2YzA4IiwiY2xpZW50X2lkIjoiZnVjayIsInNjb3BlIjpbImFsbCJdfQ.Lk0dYTha50SCq1TtAlHFcmCMUEAfbNl3g1epyLTQJJo",
       'Content-Type': "application/x-www-form-urlencoded"
       }

   response = requests.request("POST", url, headers=headers)

   print(response.text)
   ```

   Jquery:

   ```
   var settings = {
     "async": true,
     "crossDomain": true,
     "url": "http://localhost:8081/demo/test2",
     "method": "POST",
     "headers": {
       "Authorization": "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTQzOTg3MzEsInVzZXJJZCI6InVmdWNrLXVpZCIsInVzZXJfbmFtZSI6InVmdWNrIiwianRpIjoiYjI5ZDU2MDYtYmQwMC00NDExLWI2NjMtZmIyZmJmYzg2YzA4IiwiY2xpZW50X2lkIjoiZnVjayIsInNjb3BlIjpbImFsbCJdfQ.Lk0dYTha50SCq1TtAlHFcmCMUEAfbNl3g1epyLTQJJo",
       "Content-Type": "application/x-www-form-urlencoded"
     }
   }

   $.ajax(settings).done(function (response) {
     console.log(response);
   });
   ```

   Nodejs:

   ```
   var request = require("request");

   var options = { method: 'POST',
     url: 'http://localhost:8081/demo/test2',
     headers: 
      { 'Content-Type': 'application/x-www-form-urlencoded',
        Authorization: 'bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTQzOTg3MzEsInVzZXJJZCI6InVmdWNrLXVpZCIsInVzZXJfbmFtZSI6InVmdWNrIiwianRpIjoiYjI5ZDU2MDYtYmQwMC00NDExLWI2NjMtZmIyZmJmYzg2YzA4IiwiY2xpZW50X2lkIjoiZnVjayIsInNjb3BlIjpbImFsbCJdfQ.Lk0dYTha50SCq1TtAlHFcmCMUEAfbNl3g1epyLTQJJo' } };

   request(options, function (error, response, body) {
     if (error) throw new Error(error);

     console.log(body);
   });

   ```

   返回值示例(注:此返回值仅为测试):

   ```
   ok
   ```

4. 未登录前调用后端服务接口返回值示例
   ```
   {
       "error": "unauthorized",
       "error_description": "Full authentication is required to access this resource"
   }
   ```

5. 调用后端服务接口token错误返回值示例
   ```
   {
       "error": "invalid_token",
       "error_description": "11eyJhbGciOiJIUzI122NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1MSIsInNjb3BlIjpbImFsbCJdLCJleHAiOjE1MzkwOTc1NDAsInVzZXJJZCI6InUxIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9URVNUIl0sImp0aSI6IjgzMzJmNWNhLTQ5YmMtNDk3Yy04MDkyLWIyNWNmY2E1YmQyNyIsImNsaWVudF9pZCI6ImhhaGEifQ.neLH9B2Bn9gyRd0KEr1IKbwjqTU7nEM3bYXJ-JvNNf8"
   }
   ```
   
6. 调用后端服务接口权限不足错误返回值示例
   ```
   {
       "timestamp": 1539075243574,
       "status": 403,
       "error": "Forbidden",
       "message": "No message available",
       "path": "/demo/test2"
   }
   ```

7. 注销

   ​	注：header中Authorization 值为bearer +空格 + access_token值

   返回响应：

   ​	HTTP状态值 200

   请求示例：

   HTTP:

   ```
   POST /login/logout HTTP/1.1
   Host: localhost:8084
   Authorization: bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiIxIiwic2NvcGUiOlsiYWxsIl0sImV4cCI6MTUzOTI4ODUzNSwidXNlcklkIjoiMSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVEVTVCIsIlJPTEVfVEVTVDIiXSwianRpIjoiYTM0OGRmZmEtN2QxMC00MDdkLTk4OWUtYjI2Mjk1ZTkwN2M0IiwiY2xpZW50X2lkIjoiaGFoYSJ9.ALjfIOK--3re9-F6BsABP5UCrAx15SUe0yxqxjA9Z6I
   Cache-Control: no-cache
   Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW

   ------WebKitFormBoundary7MA4YWxkTrZu0gW
   Content-Disposition: form-data; name="userId"

   u1
   ------WebKitFormBoundary7MA4YWxkTrZu0gW--
   ```

   CURL:

   ```
   curl -X POST \
     http://localhost:8084/login/logout \
     -H 'Authorization: bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiIxIiwic2NvcGUiOlsiYWxsIl0sImV4cCI6MTUzOTI4ODUzNSwidXNlcklkIjoiMSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVEVTVCIsIlJPTEVfVEVTVDIiXSwianRpIjoiYTM0OGRmZmEtN2QxMC00MDdkLTk4OWUtYjI2Mjk1ZTkwN2M0IiwiY2xpZW50X2lkIjoiaGFoYSJ9.ALjfIOK--3re9-F6BsABP5UCrAx15SUe0yxqxjA9Z6I' \
     -H 'Cache-Control: no-cache' \
     -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
   ```

   Java:

   ```
   OkHttpClient client = new OkHttpClient();

   Request request = new Request.Builder()
     .url("http://localhost:8084/login/logout")
     .post(null)
     .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
     .addHeader("Authorization", "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiIxIiwic2NvcGUiOlsiYWxsIl0sImV4cCI6MTUzOTI4ODUzNSwidXNlcklkIjoiMSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVEVTVCIsIlJPTEVfVEVTVDIiXSwianRpIjoiYTM0OGRmZmEtN2QxMC00MDdkLTk4OWUtYjI2Mjk1ZTkwN2M0IiwiY2xpZW50X2lkIjoiaGFoYSJ9.ALjfIOK--3re9-F6BsABP5UCrAx15SUe0yxqxjA9Z6I")
     .addHeader("Cache-Control", "no-cache")
     .build();

   Response response = client.newCall(request).execute();
   ```

   Python:

   ```
   import requests

   url = "http://localhost:8084/login/logout"

   payload = ""
   headers = {
       'content-type': "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW",
       'Authorization': "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiIxIiwic2NvcGUiOlsiYWxsIl0sImV4cCI6MTUzOTI4ODUzNSwidXNlcklkIjoiMSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVEVTVCIsIlJPTEVfVEVTVDIiXSwianRpIjoiYTM0OGRmZmEtN2QxMC00MDdkLTk4OWUtYjI2Mjk1ZTkwN2M0IiwiY2xpZW50X2lkIjoiaGFoYSJ9.ALjfIOK--3re9-F6BsABP5UCrAx15SUe0yxqxjA9Z6I",
       'Cache-Control': "no-cache"
       }

   response = requests.request("POST", url, data=payload, headers=headers)

   print(response.text)
   ```

   Jquery:

   ```
   var form = new FormData();

   var settings = {
     "async": true,
     "crossDomain": true,
     "url": "http://localhost:8084/login/logout",
     "method": "POST",
     "headers": {
       "Authorization": "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiIxIiwic2NvcGUiOlsiYWxsIl0sImV4cCI6MTUzOTI4ODUzNSwidXNlcklkIjoiMSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVEVTVCIsIlJPTEVfVEVTVDIiXSwianRpIjoiYTM0OGRmZmEtN2QxMC00MDdkLTk4OWUtYjI2Mjk1ZTkwN2M0IiwiY2xpZW50X2lkIjoiaGFoYSJ9.ALjfIOK--3re9-F6BsABP5UCrAx15SUe0yxqxjA9Z6I",
       "Cache-Control": "no-cache"
     },
     "processData": false,
     "contentType": false,
     "mimeType": "multipart/form-data",
     "data": form
   }

   $.ajax(settings).done(function (response) {
     console.log(response);
   });
   ```

   Nodejs:

   ```
   var request = require("request");

   var options = { method: 'POST',
     url: 'http://localhost:8084/login/logout',
     headers: 
      { 'Cache-Control': 'no-cache',
        Authorization: 'bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiIxIiwic2NvcGUiOlsiYWxsIl0sImV4cCI6MTUzOTI4ODUzNSwidXNlcklkIjoiMSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVEVTVCIsIlJPTEVfVEVTVDIiXSwianRpIjoiYTM0OGRmZmEtN2QxMC00MDdkLTk4OWUtYjI2Mjk1ZTkwN2M0IiwiY2xpZW50X2lkIjoiaGFoYSJ9.ALjfIOK--3re9-F6BsABP5UCrAx15SUe0yxqxjA9Z6I',
        'content-type': 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' },
     formData: {} };

   request(options, function (error, response, body) {
     if (error) throw new Error(error);

     console.log(body);
   });

   ```

   ​

#### 备注

如果你有好的意见或建议，欢迎给我们提issue或pull request。
