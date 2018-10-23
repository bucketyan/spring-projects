### 微服务（+权限认证）

![](https://img.shields.io/badge/language-java-orange.svg) ![](https://img.shields.io/badge/qq-923260818-brightgreen.svg) ![](https://img.shields.io/badge/build-%20passing-brightgreen.svg) ![](https://img.shields.io/badge/created-October-brightgreen.svg)



#### 概述

此项目是基于Spring Cloud的微服务化开发基础框架，可以作为后端服务的开发脚手架。适合学习和直接项目中使用。

#### 项目结构

```
├─framework
│  │  
│  ├─framework-auth--------------服务鉴权中心
│  │ 
│  ├─framework-config------------统一配置中心
│  │ 
│  ├─framework-eureka------------服务注册中心
│  │ 
│  └─framework-gateway-----------网关负载中心
│ 

├─demo
│  │  
│  ├─service---------------示例应用
│  │ 
│  └─service-provider------示例应用数据提供者
│ 

├─login -----------------登录、权限服务
```

#### 启动指南

##### 环境须知

1. git服务

2. jdk1.7+

##### 运行步骤

1. 运行数据库脚本。数据库脚本位置在framework-auth目录下，依次导入framework-auth/oauth2.sql，framework-auth/sys.sql。

2. 修改配置数据库配置。修改配置中心config-repo/framework/auth-test.yml、config-repo/service/service-provider-test.yml

3. 按顺序启动运行Application类


#### 架构图

![page1](https://user-images.githubusercontent.com/28727971/47286175-2dc54280-d620-11e8-80c2-ee26e3339053.png)

无法预览图片时，见附件架构图.png

#### 说明

1. 各服务均会在服务注册中心注册。
2. 网关中心、各应用服务启动时会从配置中心拉取配置文件。
3. 不管通过网关请求哪个后端服务，都需要先登录，否则提示unauthorized。
4. 首次登录请求token，网关会放行登录请求转发至auth服务进行JWT token获取，同时auth服务会调用login服务进行用户名、密码验证及用户对应权限信息获取。
5. auth服务扩展oauth2 token返回，添加login服务获取的对应的user_id。
6. 访问后端服务时，需在header中添加Authorization 值为bearer +空格 + access_token值。
7. 访问后端服务时，网关侧会判断是否为放行URL，若为自定义放行URL直接放行。非放行URL，根据TOKEN获取userId，判定是否具有该接口的权限，权限不足则返回403，权限校验通过，转发至auth进行token校验，校验不通过返回invalid_token，校验通过转发至具体服务。
8. 若access_token失效，需通过refresh_token获取新的token值。
9. 使用header中Authorization 值为bearer +空格 + access_token值，登出注销信息。
10. 接口服务(例：demo/service)不直连数据源，需要从数据源获取数据时会调用接口服务数据服务提供者（例：demo/service-provider）。
11. 接口服务（例：demo/service）自定义数据服务调用熔断保护规则。
12. 新建新的服务应用可参考login、demo。


#### 预留接口

1. login服务 /login 

   作用：验证用户名、密码是否正确

​       参数

| 参数名称 | 类型   | 参数说明 | 示例值   |
| -------- | ------ | -------- | -------- |
| username | String | 用户名   | u1       |
| password | String | 密码     | password |

返回内容类型：Map<String, Object>

验证成功返回内容 key: userId
验证失败返回内容 key: errorMsg

示例返回值：

```
{userId=u1}
{errorMsg=password invalid}
```

2. login服务 /getAllPermission 

   作用：获取系统内所有权限信息

   无获取参数

   返回内容类型：List<Map<String, Object>>

   返回内容主要是：

   key: name (权限角色名)

   key: url (路径)

   示例返回值：

   ```
   [{name=ROLE_TEST, description=demo服务1-1, id=1, url=/service/demo/test}, {name=ROLE_TEST2, description=demo服务1-2, id=2, url=/service/demo/test2}]
   ```

3. login服务 /getAllPermissionByUserId 

   作用：根据用户ID获取用户对应的所有权限信息

   参数

   | 参数名称 | 类型   | 参数说明 | 示例值 |
   | -------- | ------ | -------- | ------ |
   | userId   | String | 用户id   | u1     |

   返回内容类型：List<Map<String, Object>>

   返回内容主要是：

   key: name (权限角色名)

   key: url (路径)

   示例返回值：

   ```
   [{name=ROLE_TEST, description=demo服务1-1, id=1, url=/service/demo/test}]
   ```

   ​

#### 请求示例

1. ##### 登陆获取token

   | 参数名称      | 类型   | 参数说明                | 示例值     |
   | ------------- | ------ | ----------------------- | ---------- |
   | client_id     | String | 客户端id                | abcdid     |
   | client_secret | String | 客户端秘钥              | abcdsecret |
   | username      | String | 用户名                  | uuser      |
   | password      | String | 用户密码                | pword      |
   | grant_type    | String | 授权模式,固定为password | password   |

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

2. ##### 刷新token

| 参数名称      | 类型   | 参数说明                              | 示例值        |
| ------------- | ------ | ------------------------------------- | ------------- |
| client_id     | String | 客户端id                              | abcdid        |
| client_secret | String | 客户端秘钥                            | abcdsecret    |
| refresh_token | String | 用于刷新Access Token用的Refresh Token | uuser         |
| grant_type    | String | 操作类型,固定为refresh_token          | refresh_token |

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

3. ##### 请求后端服务接口

| 参数名称     | 类型   | 参数说明     | 示例值                                                       |
| ------------ | ------ | ------------ | ------------------------------------------------------------ |
| access_token | String | access_token | eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTQzMDEzODUsInVzZXJJZCI6InVzZXIxLXVpZCIsInVzZXJfbmFtZSI6InVzZXIxIiwianRpIjoiMjE3ZjgzNGUtMTVjZi00YzUyLTkwZGMtNjU2OGRhZDIzYmNmIiwiY2xpZW50X2lkIjoiZnVjayIsInNjb3BlIjpbImFsbCJdfQ.thfwWjkIWZ0beYGfSwkaX7H1uUpwI2YrLEa-WWt7a90 |

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

4. ##### 未登录前调用后端服务接口返回值示例

```
{
    "error": "unauthorized",
    "error_description": "Full authentication is required to access this resource"
}
```

5. ##### 调用后端服务接口token错误返回值示例

```
{
    "error": "invalid_token",
    "error_description": "11eyJhbGciOiJIUzI122NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1MSIsInNjb3BlIjpbImFsbCJdLCJleHAiOjE1MzkwOTc1NDAsInVzZXJJZCI6InUxIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9URVNUIl0sImp0aSI6IjgzMzJmNWNhLTQ5YmMtNDk3Yy04MDkyLWIyNWNmY2E1YmQyNyIsImNsaWVudF9pZCI6ImhhaGEifQ.neLH9B2Bn9gyRd0KEr1IKbwjqTU7nEM3bYXJ-JvNNf8"
}
```

6. ##### 调用后端服务接口权限不足错误返回值示例

```
{
    "timestamp": 1539075243574,
    "status": 403,
    "error": "Forbidden",
    "message": "No message available",
    "path": "/demo/test2"
}
```

7. ##### 注销

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

#### 备注

如果你有好的意见或建议，欢迎给我们提issue或pull request。