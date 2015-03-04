# KS3 SDK For Java使用指南 
---
## 注意
文档中的示例代码仅供参考之用，具体使用的时候请参考KS3 API文档，根据自己的实际情况调节参数。  
直接使用示例代码中的参数可能会导致最后得到的结果和用户期望的不一致。

[TOC]

## 1 概述
此SDK适用于Java 5及以上版本。基于KS3 API 构建。使用此 SDK 构建您的网络应用程序，能让您以非常便捷地方式将数据安全地存储到金山云存储上。无论您的网络应用是一个网站程序，还是包括从云端（服务端程序）到终端（手持设备应用）的架构的服务或应用，通过KS3存储及其 SDK，都能让您应用程序的终端用户高速上传和下载，同时也让您的服务端更加轻盈。  
com.ksyun.ks3:几个在上传时用到的特殊的流  
com.ksyun.ks3.config:sdk客户端配置  
com.ksyun.ks3.dto:数据传输对象  
com.ksyun.ks3.exception:异常  
com.ksyun.ks3.exception.serviceside:ks3服务端的异常  
com.ksyun.ks3.http:http相关内容及对Apache Http Client的封装  
com.ksyun.ks3.service:Ks3客户端，所有的对API的操作都是在这里进行的  
com.ksyun.ks3.service.request:对API请求时参数的封装，用户进行使用时首先应该实例化并配置一个request然后通过    com.ksyun.ks3.service包下的客户端进行操作  
com.ksyun.ks3.service.response:对请求API返回的结果的解析器  
com.ksyun.ks3.signer:签名生成器，具体使用哪个签名生成器可以在ClientConfig中配置(一般情况下请勿修改)  
com.ksyun.ks3.utils:工具包  

## 2 环境准备
配置Java 5 以上开发环境  
下载KS3 SDK For Java  
添加Maven依赖

    <dependency>
        <groupId>com.ksyun.ks3</groupId>
        <artifactId>ks3-kss-java-sdk</artifactId>
        <version>0.3.0-SNAPSHOT</version>
    </dependency>
    
## 3 初始化
### 3.1 配置参数
用户可以跳过参数配置，一般情况下默认配置可以满足大部分的需求。
#### 3.1.1 配置方法一

	ClientConfig config = ClientConfig.getConfig();
	config.set(ClientConfig.CONNECTION_TIMEOUT, "50000");//连接超时时间
	config.set(ClientConfig.SOCKET_TIMEOUT,"50000");//socket超时时间
	config.set(ClientConfig.SOCKET_SEND_BUFFER_SIZE_HINT,"8192");//socket发送数据时buffer大小
	config.set(ClientConfig.SOCKET_RECEIVE_BUFFER_SIZE_HINT,"8192");//socket接受数据时buffer大小
	config.set(ClientConfig.MAX_RETRY,"5");//请求超时或发送错误时重试次数
	config.set(ClientConfig.CONNECTION_TTL,"-1");//连接池中请求的过期时间。当请求需要重试时，使用这个参数检测是否可用。
	config.set(ClientConfig.MAX_CONNECTIONS,"50");//连接池最大连接数
	config.set(ClientConfig.PROXY_HOST,null);//代理host
	config.set(ClientConfig.PROXY_PORT,"-1");//代理端口
	config.set(ClientConfig.PROXY_DAMAIN,null);//Windows domain name for configuration an NTLM proxy
	config.set(ClientConfig.PROXY_PASSWORD,null);//代理用户名
	config.set(ClientConfig.PROXY_USER_NAME,null);//代理密码
	config.set(ClientConfig.PROXY_WORKSTATION,null);//Windows workstation name for configuring NTLM proxy
	config.set(ClientConfig.IS_PREEMPTIVE_BASIC_PROXY_AUTH,"false");//Whether to authenticate preemptively against proxy server.
	config.set(ClientConfig.END_POINT,"kss.ksyun.com");//KS3服务器域名
	config.set(ClientConfig.CLIENT_SIGNER,"com.ksyun.ks3.signer.DefaultSigner");//配置签名算法生成器，一般情况下请勿修改。
	config.set(ClientConfig.CLIENT_URLFORMAT, "0"); //设置 url格式.0 {bucket}.kss.ksyun.com/{key}.1 kss.ksyun.com/{bucket}/{key}
#### 3.1.2配置方法二
新建配置文件config.properties

	httpclient.connectionTimeout=50000//连接超时时间
	httpclient.socketTimeout=50000//socket超时时间
	httpclient.socketSendBufferSizeHint=8192//socket发送数据时buffer大小
	httpclient.socketReceiveBufferSzieHint=8192//socket接受数据时buffer大小
	httpclient.maxRetry=5//请求超时或发送错误时重试次数
	httpclient.connnetionTTL=-1//连接池中请求的过期时间。当请求需要重试时，使用这个参数检测是否可用。
	httpclient.maxConnections=50//连接池最大连接数
	httpclient.proxyHost=null//代理host
	httpclient.proxyPort=-1//代理端口
	httpclient.ProxyUserName=null//代理用户名
	httpclient.ProxyPassword=null//代理密码
	httpclient.ProxyDomain=null//Windows domain name for configuration an NTLM proxy
	httpclient.ProxyWorkStation=null//Windows workstation name for configuring NTLM proxy
	httpclient.isPreemptiveBasicProxyAuth=false//Whether to authenticate preemptively against proxy server. 
	ks3client.endpoint=kss.ksyun.com//KS3服务器域名
	ks3client.signer=com.ksyun.ks3.signer.DefaultSigner//配置签名算法生成器，一般情况下请勿修改。
	ks3client.urlformat=0//设置 url格式.0 {bucket}.kss.ksyun.com/{key}.1 kss.ksyun.com/{bucket}/{key}

新建类
	
	public class DemoConfigLoader implements ConfigLoader{
		public ClientConfig load(ClientConfig config) {
			依次循环调用config.set(String key, String value)方法将配置文件中的键值对设置进去
			return config;
		}
	}

在代码初始化时

	ClientConfig.addConfigLoader(new DemoConfigLoader());
### 3.2 配置日志
该SDK使用log4j，请用户自行配置log4j.properties  
示例配置:

		log4j.logger.com.ksyun.ks3=DEBUG,stdout
		log4j.logger.org.apache.http=DEBUG,stdout
		log4j.logger.org.apache.http.wire=ERROR,stdout

		log4j.addivity.org.apache=true
		log4j.appender.stdout=org.apache.log4j.ConsoleAppender
		log4j.appender.stdout.Target=System.out
		log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
		log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss SSS} %-5p [%t]%C{1}.%M(%L) - %m%n
### 3.3 获取秘钥
1、开通KS3服务，[http://www.ksyun.com/user/register](http://www.ksyun.com/user/register) 注册账号  
2、进入控制台, [http://ks3.ksyun.com/console.html#/setting](http://ks3.ksyun.com/console.html#/setting) 获取AccessKeyID 、AccessKeySecret
### 3.4 初始化客户端
当以上全部完成之后用户便可初始化客户端进行操作了  

	Ks3 client = new Ks3Client("<您的AccessKeyID>","<您的AccessKeySecret>");
## 4 公共异常说明
### 4.1 Ks3ServiceException
当抛出Ks3ServiceException时表示KS3服务端返回异常信息。Ks3ServiceException继承自RuntimeException

| 异常      |    说明 |
| :-------- | :--------|
| SignatureDoesNotMatchException  | 用户签名验证未能验证通过 |
|InvalidAccessKeyIdException|用户提供的AccessKeyId不存在|
|RequestTimeTooSkewedException|客户端发送请求的时间和服务端接收到请求的时间差距过大|
|AccessDeniedException|用户没有权限访问当前资源|
|NoSuchBucketException|若用户请求中包含Bucket，但是该bucket在服务器上不存在|
|NoSuchKeyException|若用户请求中包含Bucket和Object Key，但是该object Key在对应的Bucket中不存在|
|NotImplementedException|您提供的一个功能性的Header尚未实现，正常使用SDK时不应该抛出|
|NotFoundApiException|不存在这个API，正常使用SDK时不应该抛出|
|URLExpiredException|这个URL已经过期|
|InternalErrorException|服务器内部错误，稍后再试或联系我们|
### 4.2 Ks3ClientException
当抛出Ks3ClientException时表示客户端发送了异常。Ks3ClientException继承自RuntimeException

| 异常      |    说明 |
| :-------- | :--------|
|ClientIllegalArgumentException|客户端参数校验失败,这个异常会代替许多继承自Ks3ServiceException且抛出原因是服务端参数校验失败的异常抛出|
|ClientHttpException|客户端连接到KS3服务器出现异常，请检查网络连接或稍后再试|
## 5 使用示例
### 5.1 Service接口

#### 5.1.1 GET Service(List Buckets)
罗列当前账户下拥有的bucket
##### 5.1.1.1 使用示例
列出当前用户的所有bucket,可以查看每个bucket的名称、创建时间以及所有者

	public List<Bucket> listBuckets(){
		List<Bucket> buckets = client.listBuckets();

		for(Bucket bucket:buckets){
			//获取bucket的创建时间
			bucket.getCreationDate();
			//获取bucket的名称
			bucket.getName();
			//获取bucket的拥有者（用户ID base64后的值）
			bucket.getOwner();
		}
		
		return buckets;
	}
##### 5.1.1.2 特殊异常
该方法不会抛出特殊异常
### 5.2 Bucket接口
#### 5.2.1 DELETE Bucket
删除bucket
##### 5.2.1.1 使用示例
删除一个Bucket

	public void deleteBucket(){
		client.deleteBucket("<您的bucket名称>");
	}
##### 5.2.1.2 特殊异常

| 异常      |    说明 |
| :-------- | :--------|
|BucketNotEmptyException|这个bucket不为空，无法删除，需要用户先调用client.clearBucket(bucket)方法清空bucket|
#### 5.2.2 DELETE Bucket cors
删除跨域配置
##### 5.2.2.1 使用示例

删除bucket的跨域资源共享规则

	public void deleteBucketCors(){
		client.deleteBucketCors("test.bucket");
	}

##### 5.2.2.2 特殊异常
该方法不会抛出特殊异常
#### 5.2.3 GET Bucket(List Objects)
罗列bucket下的文件
##### 5.2.3.1 使用示例

	/**
	 * 列出一个bucket下的object，返回的最大数为1000条
	 */
	public ObjectListing listObjectsSimple(){
		ObjectListing list = client.listObjects("<您的bucket名称>");
		return list;
	}
	/**
	 * 将列出bucket下满足object key前缀为指定字符串的object，返回的最大数为1000条
	 */
	public ObjectListing listObjectsWithPrefix(){
		ObjectListing list = client.listObjects("<您的bucket名称>","<object key前缀>");
		return list;
	}
	/**
	 * 自己调节列出object的参数，
	 */
	public ObjectListing listObjectsUseRequest(){
		ObjectListing list = null;
		//新建一个ListObjectsRequest
		ListObjectsRequest request = new ListObjectsRequest("<您的bucket名称>");
		//设置参数
		request.setMaxKeys("<max keys>");//指定返回条数最大值
		request.setPrefix("<object key前缀>");//返回以指定前缀开头的object
        request.setDelimiter("<delimiter>");//设置文件分隔符，系统将根据该分隔符组织文件夹结构，默认是"/"
		//执行操作
		client.listObjects(request);
		return list;
	}
	/**
	 * 使用循环列出所有object
	 */
	public void listAllObjects(){
		ObjectListing list = null;
		//初始化一个请求
		ListObjectsRequest request = new ListObjectsRequest("<您的bucket名称>");
		do{
			//isTruncated为true时表示之后还有object，所以应该继续循环
			if(list!=null&&list.isTruncated())
				//在ObjectListing中将返回下次请求的marker
		    	request.setMarker(list.getNextMarker());
			list = client.listObjects(request);
		}while(list.isTruncated());
	}

##### 5.2.3.2 特殊异常
该方法不会抛出特殊异常

#### 5.2.4 GET Bucket acl
获取访问权限
##### 5.2.4.1 使用示例

	public AccessControlPolicy getBucketAcl(){
		AccessControlPolicy acl = null;
		//只需要传入这个bucket的名称即可
		acl = client.getBucketACL("<您的bucket名称>");
		return acl;
	}
##### 5.2.4.2 特殊异常
该方法不会抛出特殊异常
#### 5.2.5 GET Bucket cors
获取跨域配置
##### 5.2.5.1 使用示例
获取bucket的跨域资源共享配置

	public BucketCorsConfiguration getBucketCors(){
		BucketCorsConfiguration config = client.getBucketCors("test.bucket");
		List<CorsRule> rules = config.getRules();
		for(CorsRule rule : rules){
			//控制在 OPTIONS 预取指令中 Access-Control-Request-Headers 头中指定的 header 是否允许。
			rule.getAllowedHeaders();
			//允许的跨域请求方法(GET/PUT/DELETE/POST/HEAD) 
			rule.getAllowedMethods();
			//允许跨域请求的来源 
			rule.getAllowedOrigins();
			//允许用户从应用程序中访问的响应头 
			rule.getExposedHeaders();
			//浏览器对特定资源的预取(OPTIONS)请求返回结果的缓存时间,单位为秒。 
			rule.getMaxAgeSeconds();
		}
		return config;
	}

##### 5.2.5.2 特殊异常
该方法不会抛出特殊异常
#### 5.2.6 GET Bucket location
获取bucket存储的地点
##### 5.2.6.1 使用示例
获取bucket的存储地点

	public REGION getBucketLocation(){
		//只需要传入bucket的名称
		REGION region = client.getBucketLoaction("<您的bucket名称>");
		return region;
	}
##### 5.2.6.2 特殊异常
该方法不会抛出特殊异常
#### 5.2.7 GET Bucket logging
获取bucket的日志配置
##### 5.2.7.1 使用示例
获取bucket的日志配置

	public BucketLoggingStatus getBucketLogging(){
		//只需要传入bucket的名称，由于ks3暂时对日志权限不支持，所以返回的BucketLoggingStatus中targetGrants始终为空集合
		BucketLoggingStatus logging = client.getBucketLogging("<您的bucket名称>");
		return logging;
	}
##### 5.2.7.2 特殊异常
该方法不会返回特殊异常
#### 5.2.8 HEAD Bucket
##### 5.2.8.1 使用示例
HEAD Bucket可以用来判断一个bucket是否存在

	/**
	 * Head请求一个bucket
	 */
	public HeadBucketResult headBucket() {
		HeadBucketResult result = client.headBucket("<您的bucket名称>");
		/**
		 * 通过result.getStatueCode()状态码 404则这个bucket不存在，403当前用户没有权限访问这个bucket
		 */
		return result;
	}
	/**
	 * 检测一个bucket是否存在,bucketExists内部使用的便是headBucket方法
	 */
	public boolean bucketExists(){
		return client.bucketExists("<您的bucket名称>");
	}

##### 5.2.8.2 特殊异常
该方法不会抛出特殊异常

#### 5.2.9 List Multipart Uploads
罗列没有完成的分块上传
##### 5.2.9.1 使用示例
列出当前正在执行的分块上传

	public ListMultipartUploadsResult listMultipartUploads() {
		ListMultipartUploadsRequest request = new ListMultipartUploadsRequest("test.bucket");
		/**
		 * keyMarker为空，uploadIdMarker不为空
		 * 无意义
		 * 
		 * keyMarker不为空，uploadIdMarker不为空
		 * 列出分块上传object key为keyMarker，且upload id 字典排序大于uploadIdMarker的结果
		 * 
		 * keyMarker不为空，uploadIdMarker为空
		 * 列出分块上传object key字典排序大于keyMarker的结果
		 */
		 request.setKeyMarker("keyMarker");
		 request.setUploadIdMarker("uploadIdMarker");

		 /**
		 * prefix和delimiter详解
		 * 
		 * commonPrefix由prefix和delimiter确定，以prefix开头的object
		 * key,在prefix之后第一次出现delimiter的位置之前（包含delimiter）的子字符串将存在于commonPrefixes中
		 * 比如有一下几个个分块上传
		 * 
		 * aaaa/bbb/ddd.txt
		 * aaaa/ccc/eee.txt
		 * ssss/eee/fff.txt
		 * 
		 * prefix为空 delimiter为/ 
		 * 则commonPrefix 为 aaaa/和ssss/ 返回的uploads为空
		 * 
		 * prefix为aaaa/ delimiter为/ 
		 * 则commonPrefix 为 aaaa/bbb/和aaaa/ccc/ 返回的uploads为空
		 * 
		 * prefix为ssss/ delimiter为/ 
		 * 则commonPrefix 为 aaaa/eee/ 返回的uploads为空
		 * 
		 * prefix为空 delimiter为空 
		 * 则commonPrefix 为空 返回的uploads为aaaa/bbb/ddd.txt、aaaa/ccc/eee.txt、ssss/eee/fff.txt
		 * 
		 * prefix为aaaa/ delimiter为空 
		 * 则commonPrefix 为空 返回的uploads为aaaa/bbb/ddd.txt、aaaa/ccc/eee.txt
		 * 
		 * prefix为ssss/ delimiter为空 
		 * 则commonPrefix 为空 返回的uploads为ssss/eee/fff.txt
		 * 
		 * 由于分布式文件存储系统中没有文件夹结构，所以用delimiter和prefix模拟文件夹结构,可以把prefix看成当前在哪个文件夹下，
		 * delimiter为文件夹分隔符，commonprefix为当前文件夹下的子文件夹
		 * </p>
		 */
		request.setDelimiter("/");
		request.setPrefix("prefix");
		request.setMaxUploads(100);// 最多返回100条记录，默认（最大）是1000
		ListMultipartUploadsResult result = client
				.listMultipartUploads(request);
		return result;
	}
##### 5.2.9.2 特殊异常
该方法不会抛出特殊异常
#### 5.2.10 PUT Bucket
新建bucket
##### 5.2.10.1 使用示例

	/**
	 * <p>使用最简单的方式创建一个bucket</p>
	 * <p>将使用默认的配置，权限为私有，存储地点为杭州</p>
	 */
	public void createBucketSimple(){
		client.createBucket("<您的bucket名称>");
	}
	/**
	 * <p>新建bucket的时候配置bucket的存储地点和访问权限</p>
	 */
	public void createBucketWithConfig(){
		CreateBucketRequest request = new CreateBucketRequest("<您的bucket名称>");
		//配置bucket的存储地点
		CreateBucketConfiguration config = new CreateBucketConfiguration(REGION.BEIJING);
		request.setConfig(config);
		//配置bucket的访问权限
		request.setCannedAcl(CannedAccessControlList.PublicRead);
		//执行操作
		client.createBucket(request);
	}
##### 5.2.10.2 特殊异常
|异常|说明|
| :-------- | :--------|
|InvalidBucketNameException|bucket名称不符合KS3 Bucket命名规范|
|InvalidLocationConstraintException|bucket存储地点不支持。正常使用SDK时不应该抛出|
|BucketAlreadyExistsException|该bucket名称已经存在。bucket名称是全局唯一的|
|TooManyBucketsException|用户的bucket数超过了最大限制|

#### 5.2.11 PUT Bucket acl
设置访问权限
##### 5.2.11.1 使用示例
设置bucket的访问权限

	public void putBucketAclWithCannedAcl(){
		PutBucketACLRequest request = new PutBucketACLRequest("<您的bucket名称>");
		//设为私有
		request.setCannedAcl(CannedAccessControlList.Private);
		//设为公开读 
		//request.setCannedAcl(CannedAccessControlList.PublicRead);
		//设为公开读写
		//request.setCannedAcl(CannedAccessControlList.PublicReadWrite);
		client.putBucketACL(request);
	}
	
	public void putBucketAclWithAcl(){
		PutBucketACLRequest request = new PutBucketACLRequest("<您的bucket名称>");

		AccessControlList acl = new AccessControlList();
		//设置用户id为12345678的用户对bucket的读权限
		Grant grant1 = new Grant();
		grant1.setGrantee(new GranteeId("12345678"));
		grant1.setPermission(Permission.Read);
		acl.addGrant(grant1);
		//设置用户id为123456789的用户对bucket完全控制
		Grant grant2 = new Grant();
		grant2.setGrantee(new GranteeId("123456789"));
		grant2.setPermission(Permission.FullControl);
		acl.addGrant(grant2);
		//设置用户id为12345678910的用户对bucket的写权限
		Grant grant3 = new Grant();
		grant3.setGrantee(new GranteeId("12345678910"));
		grant3.setPermission(Permission.Write);
		acl.addGrant(grant3);
		request.setAccessControlList(acl);
		
		client.putBucketACL(request);
	}
##### 5.2.11.2 特殊异常
|异常|说明|
| :-------- | :--------|
|InvalidArgumentException|用户没有设置CannedAccessControlList和AccessControlList|
#### 5.2.12 PUT Bucket cors
设置跨域规则
##### 5.2.12.1 使用示例
设置bucket的跨域资源共享规则 

	public void putBucketCors(){
		BucketCorsConfiguration config = new BucketCorsConfiguration();
		//资源跨域共享规则
		CorsRule rule1 = new CorsRule();
		
		//指定允许的跨域请求方法(GET/PUT/DELETE/POST/HEAD) 
		List<AllowedMethods> allowedMethods = new ArrayList<AllowedMethods>();
		allowedMethods.add(AllowedMethods.GET);
		//指定允许跨域请求的来源 
		List<String> allowedOrigins = new ArrayList<String>();
		allowedOrigins.add("http://example.com");
		//指定允许用户从应用程序中访问的响应头 
		List<String> exposedHeaders = new ArrayList<String>();
		exposedHeaders.add("x-kss-test1");
		//控制在 OPTIONS 预取指令中 Access-Control-Request-Headers 头中指定的 header 是否允许。
		List<String> allowedHeaders = new ArrayList<String>();
		allowedHeaders.add("x-kss-test"); 

		rule1.setAllowedHeaders(allowedHeaders);
		rule1.setAllowedMethods(allowedMethods);
		rule1.setAllowedOrigins(allowedOrigins);
		rule1.setExposedHeaders(exposedHeaders);
		//指定浏览器对特定资源的预取(OPTIONS)请求返回结果的缓存时间,单位为秒。 
		rule1.setMaxAgeSeconds(200);
		
		config.addRule(rule1);
		
		
		//一个bucket可以最多配置10条规则
		CorsRule rule2 = new CorsRule();
		List<AllowedMethods> allowedMethods2 = new ArrayList<AllowedMethods>();
		allowedMethods2.add(AllowedMethods.GET);
		allowedMethods2.add(AllowedMethods.POST);
		List<String> allowedOrigins2 = new ArrayList<String>();
		allowedOrigins2.add("http://example.com");
		allowedOrigins2.add("http://*.example.com");
		List<String> exposedHeaders2 = new ArrayList<String>();
		exposedHeaders2.add("x-kss-test1");
		exposedHeaders2.add("x-kss-test2");
		List<String> allowedHeaders2 = new ArrayList<String>();
		allowedHeaders2.add("x-kss-test"); 
		allowedHeaders2.add("x-kss-test2"); 
		rule2.setAllowedHeaders(allowedHeaders2);
		rule2.setAllowedMethods(allowedMethods2);
		rule2.setAllowedOrigins(allowedOrigins2);
		rule2.setExposedHeaders(exposedHeaders2);
		rule2.setMaxAgeSeconds(500);
		
		config.addRule(rule2);
		
		PutBucketCorsRequest request = new PutBucketCorsRequest("test.bucket",config);
		client.putBucketCors(request);
	}
##### 5.2.12.2 特殊异常
|异常|说明|
| :-------- | :--------|
|MaxMessageLengthExceededException|用户配置的CORS规则过大，转化为xml时超过64KB。应该尽量减少规则。|
|BadDigestException|body的MD5编码值出错，正常使用时不会抛出|
|InvalidArgumentException|用户设置的规则有误,具体规则请参看KS3 API文档|
#### 5.2.13 PUT Bucket logging
设置空间的日志配置
##### 5.2.13.1 使用示例
设置bucket的日志配置

	/**
	 * 将存储空间的操作日志存储在 <存放日志文件的bucket名称> 里面，日志文件的前缀是"logging-"
	 */
	public void putBucketLogging(){
		PutBucketLoggingRequest request = new PutBucketLoggingRequest("<您的bucket名称>");
		//开启日志
		request.setEnable(true);
		request.setTargetBucket("<存放日志文件的bucket名称>");
		//设置日志文件的前缀为logging-
		request.setTargetPrefix("logging-");
		client.putBucketLogging(request);
	}

##### 5.2.13.2 特殊异常

|异常|说明|
| :-------- | :--------|
|InvalidTargetBucketForLoggingException|用户不能把日志存储在指定的bucket中，可能是bucket不存在、或者没有权限存储日志|
|CrossLocationLoggingProhibitedException|配置日志的bucket和存储日志的bucket不在一个地方。即bucket location不同|

### 5.3 Object接口
#### 5.3.1 DELETE Object
删除文件
##### 5.3.1.1 使用示例
删除一个object

	/**
	 * 将<bucket名称>这个存储空间下的<object key>删除
	 */
	public void deleteObject(){
		client.deleteObject("<bucket名称>","<object key>");
	}

##### 5.3.1.2 特殊异常
该方法不会返回特殊异常
#### 5.3.2 DELETE Multiple Objects
删除多个文件
##### 5.3.2.1使用示例
批量删除object。返回结果将显示各个object的删除情况（是否成功，失败原因）

	public DeleteMultipleObjectsResult deleteObjects(){
		DeleteMultipleObjectsResult result = client.deleteObjects(new String[]{"objectKey1","objectKey2","objectKey2"},"<bucket名称>");
		return result;
	}
##### 5.3.2.2特殊异常
|异常|说明|
| :-------- | :--------|
|MissingContentMD5Exception|没有提供requestbody的MD5值，正常使用SDK时不应该抛出|
|MissingContentLengthException|没有提供Content-Length,正常使用SDK时不应该抛出|
|MissingRequestBodyErrorException|没有提供request body,正常使用SDK时不应该抛出|

注:request body中为一段xml，注明要删除哪些object
#### 5.3.3 GET Object
获取文件（下载文件）
##### 5.3.3.1 使用示例
GET Object为用户提供了object的下载，用户可以通过控制Range实现分块多线程下载，可以调节ResponseHeaderOverrides控制返回的header

	public GetObjectResult getObject(){
		GetObjectRequest request = new GetObjectRequest("<bucket名称>","<object key>");
		
		//重写返回的header
		ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
		overrides.setContentType("text/html");
		//.......
		overrides.setContentEncoding("gzip");
		request.setOverrides(overrides);
		//只接受数据的0-10字节。通过控制该项可以实现分块下载
		request.setRange(0,10);
		GetObjectResult result = client.getObject(request);
		
		Ks3Object object = result.getObject();
		//获取object的元数据
		ObjectMetadata meta = object.getObjectMetadata();
		//当分块下载时获取文件的实际大小，而非当前小块的大小
		Long length = meta.getInstanceLength();
		//获取object的输入流
		object.getObjectContent();
		
		return result;
	}
##### 5.3.3.2 特殊异常
|异常|说明|
| :-------- | :--------|
|InvalidRangeException|Range设置格式错误，Range正确格式：bytes=x-y,x、y为long型，且y>=x|

#### 5.3.4 GET Object acl
获取文件的访问权限
##### 5.3.4.1 使用示例

	public AccessControlPolicy getObjectAcl(){
		/**
		 * 获取 <bucket名称>这个bucket下<object key>的权限控制信息
		 */
		AccessControlPolicy policy = client.getObjectACL("<bucket名称>","<object key>");
		return policy;
	}

##### 5.3.4.2 特殊异常
这个方法不会抛出特殊异常
#### 5.3.5 HEAD Object
##### 5.3.5.1 使用示例

	public HeadObjectResult headObject() {
		HeadObjectRequest request = new HeadObjectRequest("<bucket名称>",
				"<object名称>");
		/**
		 * <p>
		 * 如果抛出{@link NotFoundException} 表示这个object不存在
		 * </p>
		 * <p>
		 * 如果抛出{@link AccessDinedException} 表示当前用户没有权限访问
		 * </p>
		 */
		HeadObjectResult result = client.headObject(request);
		// head请求可以用于获取object的元数据
		result.getObjectMetadata();
		return result;
	}
	/**
	 * 判断一个object是否存在
	 */
	public boolean objectExists() {
		try {
			HeadObjectRequest request = new HeadObjectRequest("<bucket名称>",
					"<object名称>");
			client.headObject(request);
			return true;
		} catch (NotFoundException e) {
			return false;
		}
	}

##### 5.3.5.2 特殊异常
|异常|说明|
| :-------- | :--------|
|NotFoudException|请求的资源不存在，由于Head请求不能返回body，所以客户端不知道具体的错误信息|

#### 5.3.6 POST Object
SDK中提供的POST Object可以获取POST Object时需要的KSSAccessKeyId、Policy、Signature三个表单项，具体要上传Object需要用户使用HTML表单或者使用js进行跨域上传，具体参考KS3 API文档POST Object
##### 5.3.6.1 使用示例

	/**
	  如果用户对KS3协议不是特别清楚,建议使用该方法。每次上传的时候都去获取一次最新的签名信息
	*/
	public PostObjectFormFields postObjectSimple(){
		/**
		 * 需要用户在postData和unknowValueField中提供所有的除KSSAccessKeyId, signature, file, policy外的所有表单项。否则用生成的签名上传会返回403</br>
		 * 对于用户可以确定表单值的放在 postData中，对于用户无法确定表单值的放在unknownValueField中(比如有的上传控件会添加一些表单项,但表单项的值可能是随机的)</br>
		 * 
		 */
		Map<String,String> postData = new HashMap<String,String>();
		
		postData.put("acl","public-read");
		postData.put("Cache-Control","no-cache");
		postData.put("Content-Type", "image/jpeg");
		postData.put("Content-Disposition","file;xx");
		postData.put("Content-Encoding","gzip");
		postData.put("Expires","Thu, 01 Dec 1994 16:00:00 GMT");
		//用户可以在表单值中使用${filename}占位符，上传上去后将会把${filename}自动替换成上传的文件名称
		postData.put("key","20150115/中文/${filename}");
		postData.put("success_action_status","204");
		postData.put("x-kss-meta-xx6", "xx6");
		
		List<String> unknowValueField = new ArrayList<String>();
		
		unknowValueField.add("cewiuhfew");
		unknowValueField.add("fwefwefwec");
		unknowValueField.add("wqqqqqq");
		
		PostObjectFormFields fields = client.postObject("<您的bucket名称>", "<要上传的文件名称,不包含路径信息>", postData, unknowValueField);
		
		fields.getKssAccessKeyId();
		fields.getPolicy();
		fields.getSignature();
		
		return fields;
	}
	/**
	 * 如果用户对KS3协议比较熟悉，建议使用该方法，自定义校验规则
	 */
	public PostObjectFormFields postObject(){ 
		PostPolicy policy = new PostPolicy();
		
		//指定签名过期时间 格式为2015-01-14T06:53:31.473Z 为格林时间
		policy.setExpiration("2015-01-14T06:53:31.473Z");
		
		//指定使用该签名上传时 content-length为0-100000才会成功
		PostPolicyCondition condition = new PostPolicyCondition();
		condition.setMatchingType(MatchingType.contentLengthRange);
		condition.setParamA("0");
		condition.setParamB("100000");
		policy.getConditions().add(condition);
		
		//指定objectkey必须是以test/开头的，即使用该签名只能往test/目录下传文件
		PostPolicyCondition condition1 = new PostPolicyCondition();
		condition1.setMatchingType(MatchingType.startsWith);
		condition1.setParamA("$key");
		condition1.setParamB("test/");
		policy.getConditions().add(condition1);
		
		//指定bucket必须等于testbucket，即使用该签名只能往testbucket这个bucket下传文件
		PostPolicyCondition condition2 = new PostPolicyCondition();
		condition2.setMatchingType(MatchingType.eq);
		condition2.setParamA("$bucket");
		condition2.setParamB("testbucket");
		policy.getConditions().add(condition2);
		
		PostObjectFormFields fields =client.postObject(policy);
		
		fields.getKssAccessKeyId();
		fields.getPolicy();
		fields.getSignature();
		
		return fields;
	}

#### 5.3.7 PUT Object
使用PUT方式上传文件
##### 5.3.7.1 使用示例
注意：使用第二、第三种方法之前请先查看KS3 API文档了解权限、元数据、callback、异步数据处理任务（ADP）的作用，如果用户不需要的话请勿设置。否则可能会导致一些无法预料的问题。

    /**
    *将new File("<filePath>")这个文件上传至<bucket名称>这个存储空间下，并命名为<object key>
    */
    public void putObjectSimple(){
    	PutObjectRequest request = new PutObjectRequest("<bucket名称>",
				"<object key>", new File("<filePath>"));
	    client.putObject(request);
    }
	/**
	 * 将new File("<filePath>")这个文件上传至<bucket名称>这个存储空间下，并命名为<object key>
	 * 同时设置权限、元数据和callback
	 */
	public void putObjectWithFile() {
		PutObjectRequest request = new PutObjectRequest("<bucket名称>",
				"<object key>", new File("<filePath>"));
		// 设置将要上传的object为公开读的
		request.setCannedAcl(CannedAccessControlList.PublicRead);

		ObjectMetadata meta = new ObjectMetadata();
		// 设置将要上传的object的用户元数据
		meta.setUserMeta("x-kss-meta-example", "example");
		// 设置将要上传的object的元数据
		meta.setContentType("text/html");
		meta.setContentEncoding("gzip");
		meta.setCacheControl("no-cache");
		meta.setHttpExpiresDate(new Date());
		meta.setContentDisposition("attachment; filename=fname.ext");

		request.setObjectMeta(meta);
		//设置callBack
		CallBackConfiguration config = new CallBackConfiguration();
		config.setCallBackUrl("http://10.4.2.38:19090/");//KS3服务器回调的地址
		//以下为KS3服务器访问http://10.4.2.38:19090/时body中的参数
		Map<String,MagicVariables> magicVariables = new HashMap<String,MagicVariables>();
		magicVariables.put("bucket", MagicVariables.bucket);
		magicVariables.put("createTime", MagicVariables.createTime);
		magicVariables.put("etag", MagicVariables.etag);
		magicVariables.put("key", MagicVariables.key);
		magicVariables.put("mimeType", MagicVariables.mimeType);
		magicVariables.put("objectSize", MagicVariables.objectSize);
		
		config.setBodyMagicVariables(magicVariables);
		
		Map<String,String> kssVariables = new HashMap<String,String>();
		
		kssVariables.put("user", "lijunwei");
		kssVariables.put("time", "20150222");
		kssVariables.put("location", "beijing");
		request.setCallBackConfiguration(config);


		//设置异步数据处理任务,该任务的作用是当文件上传成功后，对上传的文件进行视频转码功能（以下代码中是视频转码，当然还有其他各种各样的功能），将转码后的视频存储为“野生动物-转码.3gp”，并且将转码结果信息发送到http://10.4.2.38:19090/   。具体参考API文档，异步数据处理。
		List<Adp> adps= new ArrayList<Adp>();
		Adp adp= new Adp();
		adp.setCommand("tag=avop&f=mp4&res=1280x720&vbr=1000k&abr=128k");
		adp.setKey("野生动物-转码.3gp");
		adps.add(adp);
		request.setAdps(adps);
		request.setNotifyURL("http://10.4.2.38:19090/");
		
		client.putObject(request);
	}

	public void putObjectWithInputStream() {
		ObjectMetadata meta = new ObjectMetadata();
		// 设置将要上传的object的用户元数据
		meta.setUserMeta("x-kss-meta-example", "example");
		// 设置将要上传的object的元数据
		meta.setContentType("text/html");
		meta.setContentEncoding("gzip");
		meta.setCacheControl("no-cache");
		meta.setHttpExpiresDate(new Date());
		meta.setContentDisposition("attachment; filename=fname.ext");

		PutObjectRequest request = new PutObjectRequest("<bucket名称>",
				"<object key>", new ByteArrayInputStream("1234".getBytes()),
				meta);

		// 可以指定内容的长度，否则程序会把整个输入流缓存起来，可能导致jvm内存溢出
		meta.setContentLength(4);
		// 可以指定内容的md5摘要，程序将在ks3服务端进行md5值校验，否则程序只会在客户端进行md5值校验
		meta.setContentMD5("gdyb21LQTcIANtvYMT7QVQ==");

		AccessControlList acl = new AccessControlList();
		// 设置用户id为12345678的用户对object的读权限
		Grant grant1 = new Grant();
		grant1.setGrantee(new GranteeId("12345678"));
		grant1.setPermission(Permission.Read);
		acl.addGrant(grant1);
		// 设置用户id为123456789的用户对object完全控制
		Grant grant2 = new Grant();
		grant2.setGrantee(new GranteeId("123456789"));
		grant2.setPermission(Permission.FullControl);
		acl.addGrant(grant2);

		// 设置acl
		request.setAcl(acl);

		//设置callBack
		CallBackConfiguration config = new CallBackConfiguration();
		config.setCallBackUrl("http://10.4.2.38:19090/");//KS3服务器回调的地址
		//以下为KS3服务器访问http://10.4.2.38:19090/时body中的参数
		Map<String,MagicVariables> magicVariables = new HashMap<String,MagicVariables>();
		magicVariables.put("bucket", MagicVariables.bucket);
		magicVariables.put("createTime", MagicVariables.createTime);
		magicVariables.put("etag", MagicVariables.etag);
		magicVariables.put("key", MagicVariables.key);
		magicVariables.put("mimeType", MagicVariables.mimeType);
		magicVariables.put("objectSize", MagicVariables.objectSize);
		
		config.setBodyMagicVariables(magicVariables);
		
		Map<String,String> kssVariables = new HashMap<String,String>();
		
		kssVariables.put("user", "lijunwei");
		kssVariables.put("time", "20150222");
		kssVariables.put("location", "beijing");
		request.setCallBackConfiguration(config);


		//设置异步数据处理任务,该任务的作用是当文件上传成功后，对上传的文件进行视频转码功能（以下代码中是视频转码，当然还有其他各种各样的功能），将转码后的视频存储为“野生动物-转码.3gp”，并且将转码结果信息发送到http://10.4.2.38:19090/   。具体参考API文档，异步数据处理。
		List<Adp> adps= new ArrayList<Adp>();
		Adp adp= new Adp();
		adp.setCommand("tag=avop&f=mp4&res=1280x720&vbr=1000k&abr=128k");
		adp.setKey("野生动物-转码.3gp");
		adps.add(adp);
		request.setAdps(adps);
		request.setNotifyURL("http://10.4.2.38:19090/");

		client.putObject(request);
	}
##### 5.3.7.2 特殊异常
|异常|说明|
| :-------- | :--------|
|MissingContentLengthException|用户没有提供Content-Length，正常使用SDK时不应该抛出|
|InvalidKeyException|Object Key命名不符合KS3 object key命名规范|
|EntityTooLargeException|当次上传的大小超过了最大限制,正常使用SDK时不应该抛出|
|MetadataTooLargeException|用户元数据过大|
|ClientFileNotFoundException|用户指定的文件不存在，读取时抛出java.io.FileNotFoundException,被客户端处理为ClientFileNotFoundException|
|InvalidDigestException|服务端MD5校验失败，文件上传失败|
|ClientInvalidDigestException|客户端MD5校验失败，文件虽然上传成功但是可能有缺失或损坏,建议重新上传|
|CallbackFailException|KS3服务端回调用户提供的callbackurl时出错|
|CallbackTimeoutException|KS3服务端回调用户提供的callbackurl超时|

#### 5.3.8 PUT Object acl
设置文件的访问权限
##### 5.3.8.1 使用示例
修改object的权限控制

	public void putBucketAclWithCannedAcl(){
		PutBucketACLRequest request = new PutBucketACLRequest("<bucket名称>");
		//设为私有
		request.setCannedAcl(CannedAccessControlList.Private);
		//设为公开读 
		//request.setCannedAcl(CannedAccessControlList.PublicRead);
		//设为公开读写
		//request.setCannedAcl(CannedAccessControlList.PublicReadWrite);
		client.putBucketACL(request);
	}
	
	public void putBucketAclWithAcl(){
		PutBucketACLRequest request = new PutBucketACLRequest("<bucket名称>");

		AccessControlList acl = new AccessControlList();
		//设置用户id为12345678的用户对bucket的读权限
		Grant grant1 = new Grant();
		grant1.setGrantee(new GranteeId("12345678"));
		grant1.setPermission(Permission.Read);
		acl.addGrant(grant1);
		//设置用户id为123456789的用户对bucket完全控制
		Grant grant2 = new Grant();
		grant2.setGrantee(new GranteeId("123456789"));
		grant2.setPermission(Permission.FullControl);
		acl.addGrant(grant2);
		//设置用户id为12345678910的用户对bucket的写权限
		Grant grant3 = new Grant();
		grant3.setGrantee(new GranteeId("12345678910"));
		grant3.setPermission(Permission.Write);
		acl.addGrant(grant3);
		request.setAccessControlList(acl);
		
		client.putBucketACL(request);
	}
##### 5.3.8.2 特殊异常
这个方法不会抛出特殊异常

#### 5.3.9 PUT Object - Copy
拷贝文件
##### 5.3.9.1 使用示例

	public void copyObject(){
		/**将sourceBucket这个存储空间下的sourceKey这个object复制到destinationBucket这个存储空间下，并命名为destinationObject
		 */
		CopyObjectRequest request = new CopyObjectRequest("destinationBucket","destinationObject","sourceBucket","sourceKey");
		client.copyObject(request);
	}
	/**
	 * 重命名object
	 */
	public void renameObject(){
		/**将sourceBucket这个存储空间下的sourceKey这个object重命名为destinationObject
		 */
		CopyObjectRequest request = new CopyObjectRequest("sourceBucket","destinationObject","sourceBucket","sourceKey");
		client.copyObject(request);
	}

##### 5.3.9.2 特殊错误
|异常|说明|
| :-------- | :--------|
|MissingContentLengthException|用户没有提供Content-Length，正常使用SDK时不应该抛出|
|InvalidKeyException|Object Key命名不符合KS3 object key命名规范|
|InvalidArgumentException|没有提供sourceBucket或sourceKey,正常使用SDK时不应该抛出|
|InvalidKeyException|目标object已经存在，无法copy|

####5.3.10 PUT Adp
添加异步数据处理任务

#####5.3.10.1 使用示例

	// 添加异步数据处理任务,该任务的作用是将指定的文件进行视频转码功能（以下代码中是视频转码，当然还有其他各种各样的功能），将转码后的视频存储为“野生动物-转码.3gp”，并且将转码结果信息发送到http://10.4.2.38:19090/
	// 。具体参考API文档，异步数据处理。
	public String putAdp() {
		PutAdpRequest request = new PutAdpRequest("<您的bucket名称>",
				"<要处理的数据的object key>");
		List<Adp> adps = new ArrayList<Adp>();

		Adp adp = new Adp();
		//处理命令，具体查看API文档，异步数据处理
		adp.setCommand("tag=avop&f=mp4&res=1280x720&vbr=1000k&abr=128k");
		//处理完成后存储的key
		adp.setKey("野生动物-转码.3gp");
		adps.add(adp);

		request.setAdps(adps);
		//处理结果接受服务，具体查看API文档，异步数据处理
		request.setNotifyURL("http://10.4.2.38:19090/");
		//任务id,用于查询任务处理状态
		String id = client.putAdpTask(request);
		return id;
	}

#####5.3.10.2 特殊异常

该方法不会返回特殊异常

#### 5.3.11 GET Adp

查询异步数据处理任务

##### 5.3.11.1 使用示例

	public AdpTask getAdp(){
		AdpTask task = client.getAdpTask("<TaskID>");
		//0,"task is create fail"、1,"task is create success"、2,"task is processing"、3,"task is process success"、4,"task is process fail"
		task.getProcessstatus();
		//0,"task is not notify"、1,"task is notify success"、2,"task is notify fail"
		task.getNotifystatus();
		
		//查询每条命令的具体执行结果，包括是否执行成功，以及执行成功后存储的key
		task.getAdpInfos();
		
		return task;
	}

##### 5.3.11.2 特殊异常

|异常|说明|
| :-------- | :--------|
|QueryTaskFail|查询失败，很可能是因为提供的TaskID不存在|

#### 5.3.12 Multipart Upload
##### 5.3.12.1 使用示例
注：中途想停止分块上传的话请调用client.abortMultipartUpload(bucketname, objectkey, uploadId);
用户可以根据实际情况进行多线程分块上传


	public void multipartUploadWithFile(){
		//定义每次上传的块的大小为10M
		long part = 10 * 1024 * 1024;
		//将文件存放在test.bucket这个存储空间里
		String bucket = "test.bucket";
		//将要上传的文件的object key
		String key = "object.rar";
		//将要上传的文件路径
		String filename = "filePath";

		
		//***********************初始化分块上传*****************************************
		InitiateMultipartUploadRequest request1 = new InitiateMultipartUploadRequest(
				bucket, key);
		//设置为公开读
		request1.setCannedAcl(CannedAccessControlList.PublicRead);
		ObjectMetadata meta = new ObjectMetadata();
		//设置将要上传的object的用户元数据
		meta.setUserMeta("x-kss-meta-example", "example");
		//设置将要上传的object的元数据
		meta.setContentType("text/html");
		meta.setContentEncoding("gzip");
		meta.setCacheControl("no-cache");
		meta.setHttpExpiresDate(new Date());
		meta.setContentDisposition("attachment; filename=fname.ext");
		//设置元数据
		request1.setObjectMeta(meta);
		
		InitiateMultipartUploadResult result = client
				.initiateMultipartUpload(request1);
		//***********************执行分块上传*****************************************
		File file = new File(filename);
		//计算一共有多少块
		long n = file.length() / part;
		//依次进行上传
		for (int i = 0; i <= n; i++) {
			//初始化一个分块上传的请求
			//参数分别为：
			//bucket名称，  
            //object key，
      		//uploadId(由initMultipartUpload获得),
			//partnumber(当前上传的是第几块),  
            //要上传的完整文件,
            //单块大小(long),
			//文件偏移量(即从文件偏移量开始截取，一共截取单块大小的字节）
			UploadPartRequest request = new UploadPartRequest(
					result.getBucket(), result.getKey(), result.getUploadId(),
					i + 1, file, part, (long) i * part);
			//可以指定内容的MD5值，否则程序只会在客户端进行MD5校验。如果指定的话会在服务端进行MD5校验
			request.setContentMd5("52D04DC20036DBD8");
			client.uploadPart(request);
		}
		//***********************列出分块上传已上传的块*****************************************
		ListPartsRequest requestList = new ListPartsRequest(result.getBucket(),
				result.getKey(), result.getUploadId());
		ListPartsResult tags = client.listParts(requestList);
		//注意:当块数大于1000时，请通过控制uploadIdMarker多次调用listParts方法，最后再把所有得到的结果合并成一个
		//***********************完成分块上传，使服务端将块合并成一个文件*****************************************
		CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(
				tags);//提供了一个简便的方式
		//设置callBack
		CallBackConfiguration config = new CallBackConfiguration();
		config.setCallBackUrl("http://10.4.2.38:19090/");//KS3服务器回调的地址
		//以下为KS3服务器访问http://10.4.2.38:19090/时body中的参数
		Map<String,MagicVariables> magicVariables = new HashMap<String,MagicVariables>();
		magicVariables.put("bucket", MagicVariables.bucket);
		magicVariables.put("createTime", MagicVariables.createTime);
		magicVariables.put("etag", MagicVariables.etag);
		magicVariables.put("key", MagicVariables.key);
		magicVariables.put("mimeType", MagicVariables.mimeType);
		magicVariables.put("objectSize", MagicVariables.objectSize);
		
		config.setBodyMagicVariables(magicVariables);
		
		Map<String,String> kssVariables = new HashMap<String,String>();
		
		kssVariables.put("user", "lijunwei");
		kssVariables.put("time", "20150222");
		kssVariables.put("location", "beijing");
		request.setCallBackConfiguration(config);
		client.completeMultipartUpload(request);
	}
	public void multipartUploadWithInputStream(){
		//将文件存放在test.bucket这个存储空间里
		String bucket = "ksc-scm";
		//将要上传的文件的object key
		String key = "object.txt";

		
		//***********************初始化分块上传*****************************************
		InitiateMultipartUploadRequest request1 = new InitiateMultipartUploadRequest(
				bucket, key);
		//设置为公开读
		request1.setCannedAcl(CannedAccessControlList.PublicRead);
		ObjectMetadata meta = new ObjectMetadata();
		//设置将要上传的object的用户元数据
		meta.setUserMeta("x-kss-meta-example", "example");
		//设置将要上传的object的元数据
		meta.setContentType("text/html");
		meta.setContentEncoding("gzip");
		meta.setCacheControl("no-cache");
		meta.setHttpExpiresDate(new Date());
		meta.setContentDisposition("attachment; filename=fname.ext");
		//设置元数据
		request1.setObjectMeta(meta);
		
		InitiateMultipartUploadResult result = client
				.initiateMultipartUpload(request1);
		//***********************执行分块上传*****************************************
		//假设一共要上传50块
		long n = 50;
		//依次进行上传
		for (int i = 0; i < n; i++) {
			//生成一个大小为5M的输入流
			StringBuffer buffer = new StringBuffer();
			for(int j = 0;j < 1024*1024;j++){
				buffer.append("12345");
			}
			InputStream content = new ByteArrayInputStream(buffer.toString().getBytes());
			//初始化一个分块上传的请求
			//参数分别为：
			//bucket名称，  
            //object key，
      		//uploadId(由initMultipartUpload获得),
			//partnumber(当前上传的是第几块),  
            //要上传的输入流，
			//流大小，需要准确提供
			UploadPartRequest request = new UploadPartRequest(
					result.getBucket(), result.getKey(), result.getUploadId(),
					i + 1,content,5*1024*1024);
			//可以指定内容的MD5值，否则程序只会在客户端进行MD5校验。如果指定的话会在服务端进行MD5校验
			//request.setContentMD5("52D04DC20036DBD8");
			client.uploadPart(request);
		}  
		
		//***********************列出分块上传以上传的块*****************************************
		
		ListPartsRequest requestList = new ListPartsRequest(result.getBucket(),
				result.getKey(), result.getUploadId());
		ListPartsResult tags = client.listParts(requestList);
		//注意:当块数大于1000时，请通过控制uploadIdMarker多次调用listParts方法，最后再把所有得到的结果合并成一个
		//***********************完成分块上传，使服务端将块合并成一个文件*****************************************
		CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(
				tags);
	    //设置callBack
		CallBackConfiguration config = new CallBackConfiguration();
		config.setCallBackUrl("http://10.4.2.38:19090/");//KS3服务器回调的地址
		//以下为KS3服务器访问http://10.4.2.38:19090/时body中的参数
		Map<String,MagicVariables> magicVariables = new HashMap<String,MagicVariables>();
		magicVariables.put("bucket", MagicVariables.bucket);
		magicVariables.put("createTime", MagicVariables.createTime);
		magicVariables.put("etag", MagicVariables.etag);
		magicVariables.put("key", MagicVariables.key);
		magicVariables.put("mimeType", MagicVariables.mimeType);
		magicVariables.put("objectSize", MagicVariables.objectSize);
		
		config.setBodyMagicVariables(magicVariables);
		
		Map<String,String> kssVariables = new HashMap<String,String>();
		
		kssVariables.put("user", "lijunwei");
		kssVariables.put("time", "20150222");
		kssVariables.put("location", "beijing");
		request.setCallBackConfiguration(config);


		//设置异步数据处理任务,该任务的作用是当文件上传成功后，对上传的文件进行视频转码功能（以下代码中是视频转码，当然还有其他各种各样的功能），将转码后的视频存储为“野生动物-转码.3gp”，并且将转码结果信息发送到http://10.4.2.38:19090/   。具体参考API文档，异步数据处理。
		List<Adp> adps= new ArrayList<Adp>();
		Adp adp= new Adp();
		adp.setCommand("tag=avop&f=mp4&res=1280x720&vbr=1000k&abr=128k");
		adp.setKey("野生动物-转码.3gp");
		adps.add(adp);
		request.setAdps(adps);
		request.setNotifyURL("http://10.4.2.38:19090/");
		
		client.completeMultipartUpload(request);
	}
##### 5.3.12.2 特殊异常
Init Multipart Upload

|异常|说明|
| :-------- | :--------|
|InvalidKeyException|Object Key命名不符合KS3 object key命名规范|

Upload Part

|异常|说明|
| :-------- | :--------|
|MissingContentLengthException|用户没有提供Content-Length,正常使用SDK不应该抛出|
|NoSuchUploadException|用户提供的UploadId不存在|
|EntityTooLargeException|单块上传内容过大，正常使用SDK不应该抛出该异常|
|ClientFileNotFoundException|用户指定的文件不存在，读取时抛出java.io.FileNotFoundException,被客户端处理为ClientFileNotFoundException|
|InvalidPartNumException|partnumber不在正确范围内，正常使用SDK不应该抛出该异常|
|InvalidDigestException|服务端MD5校验失败，数据上传失败|
|ClientInvalidDigestException|客户端MD5校验失败，数据虽然上传成功但是可能有缺失或损坏|

List Parts

|异常|说明|
| :-------- | :--------|
|NoSuchUploadException|用户提供的UploadId不存在|

Abort Multipart Upload

|异常|说明|
| :-------- | :--------|
|NoSuchUploadException|用户提供的UploadId不存在|

Complete Multipart Upload

|异常|说明|
| :-------- | :--------|
|InvalidPartOrderException|partnumber需要是升序且连续的|
|NoSuchUploadException|用户提供的UploadId不存在|
|InvalidPartException|用户提供的某个块不存在或是ETag不匹配|
|EntityTooSmallException|除最后一块外的块大小 小于KS3要求的最小值|
|CallbackFailException|KS3服务端回调用户提供的callbackurl时出错|
|CallbackTimeoutException|KS3服务端回调用户提供的callbackurl超时|
