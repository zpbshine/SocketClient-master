#include"com_haocai_socketclient_SocketUtil.h"
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
//第一步：导入Socket编程的标准库
//这个标准库：linux数据类型(size_t、time_t等等......)
#include<sys/types.h>
//提供socket函数及数据结构
#include<sys/socket.h>
//数据结构(sockaddr_in)
#include<netinet/in.h>
//ip地址的转换函数
#include<arpa/inet.h>

#include <android/log.h>
//定义服务端
#define SERVER_PORT 9998

#define  LOG_TAG    "socket_client"
#define  LOGI(FORMAT,...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,FORMAT,##__VA_ARGS__);
#define  LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,FORMAT,##__VA_ARGS__);
#define  LOGD(FORMAT,...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,FORMAT, ##__VA_ARGS__);

JNIEXPORT void JNICALL Java_com_haocai_socketclient_SocketUtil_startClient
  (JNIEnv *env, jobject jobj, jstring server_ip_jstr, jint server_port){

	const char* server_ip = (*env)->GetStringUTFChars(env, server_ip_jstr, NULL);

	//客户端socket
	int client_socket_fd;

	//服务端网络地址
    struct sockaddr_in  server_addr;

    //初始化网络地址
    //参数一：传变量的地址($server_addr)
	//参数二：开始位置
	//参数三：大小
    //初始化服务端网络地址
     memset(&server_addr,0,sizeof(server_addr));
	//AF_INET:TCP/IP协议、UDP
	//AF_ISO:ISO 协议
	 server_addr.sin_family = AF_INET;
	//设置服务端IP地址(自动获取系统默认的本机IP，自动分配)
	 server_addr.sin_addr.s_addr = inet_addr(server_ip);

     //设置服务端端口
	 server_addr.sin_port = htons(server_port);

	//创建客户端
	client_socket_fd = socket(PF_INET,SOCK_STREAM,0);
	//判断是否创建成功
	if(client_socket_fd < 0){

       LOGE("create error!");
       return ;
	}

	//连接服务器
	//参数一：哪一个客户端
	//参数二：连接服务器地址
	//参数三：地址大小
	int con_result = connect(client_socket_fd,(struct sockaddr*)&server_addr,sizeof(server_addr));
    if(con_result<0){
  	LOGE("connect error!");

	return ;
	}

	//发送消息(向服务器发送内容)
	char buffer[BUFSIZ] = "Hello Socket Server!";
	//参数一：指定客户端
	//参数二：指定缓冲区(冲那里数据读取)
	//参数三：实际读取的大小strlen(buffer)(其实读取到"\0"结束)
	//参数四：从哪里开始读取
	send(client_socket_fd,buffer,strlen(buffer),0);

	//关闭
	shutdown(client_socket_fd,SHUT_RDWR);
    LOGI("client--- end-----");
    	(*env)->ReleaseStringUTFChars(env, server_ip_jstr, server_ip);
   return ;

  }
JNIEXPORT void JNICALL Java_com_haocai_socketclient_SocketUtil_startServer
  (JNIEnv *env, jobject jobj){
	  //第二步:创建socket
  //服务端的socket
  int server_socket_fd;
  //客户端
  int client_socket_fd;
  //服务端网络地址
  struct sockaddr_in  server_addr;
  //客户端网络地址
  struct sockaddr_in client_addr;

  //初始化网络地址
  //参数一：传变量的地址($server_addr)
  //参数二：开始为止 
  //参数三：大小
  //初始化服务端网络地址
  memset(&server_addr,0,sizeof(server_addr ));
  //初始化客户端网络地址
  //memset(&client_addr,0,sizeof(client_addr));
  
  //设置服务端网络地址-协议簇(sin_family)
  //AF_INET:TCP/IP协议、UDP
   //AF_ISO:ISO 协议         
  server_addr.sin_family = AF_INET;

  //设置服务端IP地址(自动获取系统默认的本机IP，自动分配)
  server_addr.sin_addr.s_addr = INADDR_ANY;
  
  //设置服务端端口
  server_addr.sin_port = htons(SERVER_PORT);
  
  //创建服务端socket 
  //参数一(family)：通信域(例如:IPV4->PF_INET、IPV6等等......)
  //参数二(type)：通信类型(例如:TCP->SOCK_STREAM,UDP->SOCK_DGRAM等等......)
  //参数三(protocol)：指定使用的协议(一般情况下都是默认为0)
  //默认为0就是使用系统默认的协议，系统支持什么我就就用什么
  //TCP->IPPROTO_TCP
  //UDP->IPPROTO_UDP
  //SCTP->IPPROTO_SCTP
   server_socket_fd = socket(PF_INET,SOCK_STREAM,0);

   //判断是否创建成功
   if(server_socket_fd <0){
       LOGE("create error!");
	  return ;
   }


    LOGE("服务器创建成功!\n");

   //服务端绑定地址
   //参数一：服务端socket
   //参数二：网络地址
   //参数三：数据类型大小
   //socketaddr和sockaddr_in```````````````
   bind(server_socket_fd,(struct sockaddr*)&server_addr,sizeof(server_addr));
   
   //监听客户端连接请求(服务端监听有没有客户端连接)
   //参数一：监听的服务器socket
   //参数二：客户端数量（未处理队列数量）
   listen(server_socket_fd,6);

   //接收客户端连接
   //参数一(sockfd):服务端
   //参数二(addr):客户端
   //参数三(addrlen):大小
  socklen_t sin_size = sizeof(struct sockaddr_in);
  //获取一个客户端
  client_socket_fd= accept(server_socket_fd,(struct sockaddr*)&client_socket_fd,&sin_size);
  //判断客户端是否连接成功
  if(client_socket_fd < 0){
	   LOGE("连接失败");
	  return ;
  }else{
  LOGE("连接成功");
  }
  //连接成功:读取客户端数据
  //BUFSIZ:默认值
  char buffer[BUFSIZ];
  int len=0;
  int i;
  while(1){
	  //参数一:读取客户端数据(数据源)
	  //参数二:读取到哪里(我们要读取到缓冲区buffer)
	  //参数三:每次读取多大BUFSIZ
	  //参数四:从哪里开始读0
      //LOGE("waiting for data");
      len = recv(client_socket_fd,buffer,BUFSIZ,0);
      //LOGE("服务端获取数据=%s\n",len);
      LOGD("len = %d\n", len);
      if(len > 0){
	    //说明读取到了数据
         //LOGE("%s\n",buffer);
          for(i=0;i<len;i++)
          {
            LOGD("buffer = %X\n", buffer[i]);

          }

          //LOGD("buffer = %d\n", buffer[0]);
	  }
  }
  //关闭服务端和客户端Socket
  //参数一：关闭的源
  //参数二：关闭的类型(设置权限)
  //SHUT_RD：关闭读(只允许写，不允许读)
  //SHUT_WR：关闭写(只允许读，不允许写)
  //SHUT_RDWR：读写都关闭(书写都不允许)
  shutdown(client_socket_fd,SHUT_RDWR);
  shutdown(server_socket_fd,SHUT_RDWR);
  

   LOGE("server end.....\n");
  getchar();
  }