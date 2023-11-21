一般情况下，无法应对高并发场景

![image-20231106220412503](https://raw.githubusercontent.com/MrSunflowers/images/main/note/images/202311062204544.png)

真正的无状态会话

使用 token 代替 session 维持会话状态

token 中包含加密后的用户信息，客户端无法解密，用户请求需携带，服务端解密后使用，一旦篡改，无法解密，请求无效