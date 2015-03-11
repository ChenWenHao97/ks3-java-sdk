package com.ksyun.ks3.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
 

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import com.ksyun.ks3.config.ClientConfig;
import com.ksyun.ks3.config.Constants;
import com.ksyun.ks3.dto.*;
import com.ksyun.ks3.dto.CreateBucketConfiguration.REGION;
import com.ksyun.ks3.dto.PostPolicyCondition.MatchingType;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.exception.Ks3ServiceException;
import com.ksyun.ks3.exception.client.ClientIllegalArgumentException;
import com.ksyun.ks3.exception.client.ClientIllegalArgumentExceptionGenerator;
import com.ksyun.ks3.exception.serviceside.NotFoundException;
import com.ksyun.ks3.http.HttpMethod;
import com.ksyun.ks3.http.Ks3CoreController;
import com.ksyun.ks3.service.request.AbortMultipartUploadRequest;
import com.ksyun.ks3.service.request.CompleteMultipartUploadRequest;
import com.ksyun.ks3.service.request.CopyObjectRequest;
import com.ksyun.ks3.service.request.CopyPartRequest;
import com.ksyun.ks3.service.request.CreateBucketRequest;
import com.ksyun.ks3.service.request.DeleteBucketCorsRequest;
import com.ksyun.ks3.service.request.DeleteBucketRequest;
import com.ksyun.ks3.service.request.DeleteMultipleObjectsRequest;
import com.ksyun.ks3.service.request.DeleteObjectRequest;
import com.ksyun.ks3.service.request.GetBucketCorsRequest;
import com.ksyun.ks3.service.request.GetBucketLocationRequest;
import com.ksyun.ks3.service.request.GetBucketLoggingRequest;
import com.ksyun.ks3.service.request.GetObjectRequest;
import com.ksyun.ks3.service.request.HeadBucketRequest;
import com.ksyun.ks3.service.request.HeadObjectRequest;
import com.ksyun.ks3.service.request.InitiateMultipartUploadRequest;
import com.ksyun.ks3.service.request.Ks3WebServiceRequest;
import com.ksyun.ks3.service.request.ListBucketsRequest;
import com.ksyun.ks3.service.request.ListMultipartUploadsRequest;
import com.ksyun.ks3.service.request.ListObjectsRequest;
import com.ksyun.ks3.service.request.ListPartsRequest;
import com.ksyun.ks3.service.request.PutBucketCorsRequest;
import com.ksyun.ks3.service.request.PutBucketLoggingRequest;
import com.ksyun.ks3.service.request.PutObjectRequest;
import com.ksyun.ks3.service.request.PutAdpRequest;
import com.ksyun.ks3.service.request.UploadPartRequest;
import com.ksyun.ks3.service.response.AbortMultipartUploadResponse;
import com.ksyun.ks3.service.response.CompleteMultipartUploadResponse;
import com.ksyun.ks3.service.response.CreateBucketResponse;
import com.ksyun.ks3.service.response.DeleteBucketResponse;
import com.ksyun.ks3.service.response.DeleteObjectResponse;
import com.ksyun.ks3.service.response.GetObjectResponse;
import com.ksyun.ks3.service.response.HeadBucketResponse;
import com.ksyun.ks3.service.response.HeadObjectResponse;
import com.ksyun.ks3.service.response.InitiateMultipartUploadResponse;
import com.ksyun.ks3.service.response.Ks3WebServiceResponse;
import com.ksyun.ks3.service.response.ListBucketsResponse;
import com.ksyun.ks3.service.response.ListObjectsResponse;
import com.ksyun.ks3.service.response.ListPartsResponse;
import com.ksyun.ks3.service.response.PutObjectResponse;
import com.ksyun.ks3.service.request.*;
import com.ksyun.ks3.service.response.*;
import com.ksyun.ks3.utils.AuthUtils;
import com.ksyun.ks3.utils.DateUtils;
import com.ksyun.ks3.utils.DateUtils.DATETIME_PROTOCOL;
import com.ksyun.ks3.utils.HttpUtils;
import com.ksyun.ks3.utils.StringUtils;

/**
 * @author lijunwei[lijunwei@kingsoft.com]  
 * 
 * @date 2014年10月14日 下午5:30:30
 * 
 * @description ks3客户端，用户使用时需要先配置{@link ClientConfig},然后初始化一个Ks3Client进行操作
 **/
public class Ks3Client implements Ks3 {
	private static final Log log = LogFactory.getLog(Ks3Client.class);
	
	private Authorization auth;

	public Authorization getAuth() {
		return auth;
	}

	public void setAuth(Authorization auth) {
		this.auth = auth;
	}

	public Ks3Client() {
	}

	public Ks3Client(Authorization auth) {
		this.auth = auth;
	}

	public Ks3Client(String accesskeyid, String accesskeysecret) {
		this.auth = new Authorization(accesskeyid, accesskeysecret);
	}

	private Ks3CoreController client = new Ks3CoreController();

	public List<Bucket> listBuckets() throws Ks3ClientException,
			Ks3ServiceException {
		return listBuckets(new ListBucketsRequest());
	}

	public List<Bucket> listBuckets(ListBucketsRequest request)
			throws Ks3ClientException, Ks3ServiceException {

		return client.execute(auth, request, ListBucketsResponse.class);
	}

	public REGION getBucketLoaction(String bucketName)
			throws Ks3ClientException, Ks3ServiceException {
		GetBucketLocationRequest request = new GetBucketLocationRequest(
				bucketName);
		return this.getBucketLoaction(request);
	}

	public REGION getBucketLoaction(GetBucketLocationRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		return this.client.execute(auth, request,
				GetBucketLocationResponse.class);
	}

	public BucketLoggingStatus getBucketLogging(String bucketName)
			throws Ks3ClientException, Ks3ServiceException {
		GetBucketLoggingRequest request = new GetBucketLoggingRequest(
				bucketName);
		return this.getBucketLogging(request);
	}

	public BucketLoggingStatus getBucketLogging(GetBucketLoggingRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		return client.execute(auth, request, GetBucketLoggingResponse.class);
	}

	public void putBucketLogging(String bucketName, boolean enable,
			String targetBucket) throws Ks3ClientException, Ks3ServiceException {
		PutBucketLoggingRequest request = new PutBucketLoggingRequest(
				bucketName, enable, targetBucket);
		this.putBucketLogging(request);

	}

	public void putBucketLogging(String bucketName, boolean enable,
			String targetBucket, String targetPrefix)
			throws Ks3ClientException, Ks3ServiceException {
		PutBucketLoggingRequest request = new PutBucketLoggingRequest(
				bucketName, enable, targetBucket, targetPrefix);
		this.putBucketLogging(request);
	}

	public void putBucketLogging(PutBucketLoggingRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		client.execute(auth, request, PutBucketLoggingResponse.class);
	}

	public AccessControlPolicy getBucketACL(String bucketName)
			throws Ks3ClientException, Ks3ServiceException {
		return getBucketACL(new GetBucketACLRequest(bucketName));
	}

	public AccessControlPolicy getBucketACL(GetBucketACLRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		return client.execute(auth, request, GetBucketACLResponse.class);
	}

	public void putBucketACL(String bucketName,
			AccessControlList accessControlList) throws Ks3ClientException,
			Ks3ServiceException {
		putBucketACL(new PutBucketACLRequest(bucketName, accessControlList));
	}

	public void putBucketACL(PutBucketACLRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		client.execute(auth, request, PutBucketACLResponse.class);
	}

	public void putObjectACL(String bucketName, String objectName,
			AccessControlList accessControlList) throws Ks3ClientException,
			Ks3ServiceException {
		putObjectACL(new PutObjectACLRequest(bucketName, objectName,
				accessControlList));
	}

	public void putObjectACL(PutObjectACLRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		client.execute(auth, request, PutObjectACLResponse.class);
	}

	public AccessControlPolicy getObjectACL(String bucketName, String objectName)
			throws Ks3ClientException, Ks3ServiceException {
		return getObjectACL(new GetObjectACLRequest(bucketName, objectName));
	}

	public AccessControlPolicy getObjectACL(GetObjectACLRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		return client.execute(auth, request, GetObjectACLResponse.class);
	}

	public Bucket createBucket(String bucketname) throws Ks3ClientException,
			Ks3ServiceException {
		return createBucket(new CreateBucketRequest(bucketname));
	}

	public Bucket createBucket(CreateBucketRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		Bucket bucket = client.execute(auth, request,
				CreateBucketResponse.class);
		bucket.setName(request.getBucketname());
		return bucket;
	}

	public void clearBucket(String bucketName) throws Ks3ClientException,
			Ks3ServiceException {
		this.removeDir(bucketName, null);
	}

	public void makeDir(String bucketName, String dir)
			throws Ks3ClientException, Ks3ServiceException {
		if (!dir.endsWith("/"))
			throw new IllegalArgumentException("dir should be end with /");
		PutObjectRequest request = new PutObjectRequest(bucketName, dir,
				new ByteArrayInputStream(new byte[] {}), null);
		this.putObject(request);
	}

	public void removeDir(String bucketName, String dir)
			throws Ks3ClientException, Ks3ServiceException {
		if (dir != null && !dir.endsWith("/") && !StringUtils.isBlank(dir))
			throw new IllegalArgumentException(
					"dir should be end with / or null(clear bucket)");
		String marker = null;
		ObjectListing list = null;
		do {
			ListObjectsRequest request = new ListObjectsRequest(bucketName);
			request.setPrefix(dir);
			request.setMarker(marker);
			list = this.listObjects(request);
			marker = list.getNextMarker();

			List<String> keys = new ArrayList<String>();
			for (Ks3ObjectSummary obj : list.getObjectSummaries()) {
				keys.add(obj.getKey());
			}
			if (keys.size() > 0)
				this.deleteObjects(keys, bucketName);
		} while (list.isTruncated());
		if (dir != null) {
			boolean exists = true;
			try {
				headObject(bucketName, dir);
			} catch (NotFoundException e) {
				exists = false;
			}
			if (exists)
				deleteObject(bucketName, dir);
		}
	}

	public void deleteBucket(String bucketname) throws Ks3ClientException,
			Ks3ServiceException {
		deleteBucket(new DeleteBucketRequest(bucketname));

	}

	public void deleteBucket(DeleteBucketRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		client.execute(auth, request, DeleteBucketResponse.class);
	}

	public ObjectListing listObjects(String bucketname)
			throws Ks3ClientException, Ks3ServiceException {
		return listObjects(new ListObjectsRequest(bucketname, null, null, null,
				null));
	}

	public ObjectListing listObjects(String bucketname, String prefix)
			throws Ks3ClientException, Ks3ServiceException {
		return listObjects(new ListObjectsRequest(bucketname, prefix, null,
				null, null));
	}

	public ObjectListing listObjects(ListObjectsRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		return client.execute(auth, request, ListObjectsResponse.class);
	}

	public void deleteObject(String bucketname, String key)
			throws Ks3ClientException, Ks3ServiceException {
		deleteObject(new DeleteObjectRequest(bucketname, key));
	}

	public void deleteObject(DeleteObjectRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		client.execute(auth, request, DeleteObjectResponse.class);
	}

	public GetObjectResult getObject(String bucketname, String key)
			throws Ks3ClientException, Ks3ServiceException {
		return getObject(new GetObjectRequest(bucketname, key));
	}

	public GetObjectResult getObject(GetObjectRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		String objectkey = request.getObjectkey();
		GetObjectResult object = client.execute(auth, request,
				GetObjectResponse.class);
		object.getObject().setBucketName(request.getBucketname());
		object.getObject().setKey(objectkey);
		return object;
	}

	public String generatePresignedUrl(String bucket, String key, int expiration)
			throws Ks3ClientException {
		return generatePresignedUrl(bucket, key, expiration, null);
	}

	@SuppressWarnings("deprecation")
	public String generatePresignedUrl(String bucket, String key,
			int expiration, ResponseHeaderOverrides overrides)
			throws Ks3ClientException {
		if (overrides == null)
			overrides = new ResponseHeaderOverrides();
		boolean isPrivate = false;
		AccessControlList acl = this.getObjectACL(bucket, key)
				.getAccessControlList();
		final Collection<Permission> allUsersPermissions = new LinkedHashSet<Permission>();
		for (final Grant grant : acl.getGrants()) {
			if (GranteeUri.AllUsers.equals(grant.getGrantee())) {
				allUsersPermissions.add(grant.getPermission());
			}
		}
		final boolean read = allUsersPermissions.contains(Permission.Read);
		final boolean write = allUsersPermissions.contains(Permission.Write);
		if (read && write) {
			isPrivate = false;
		} else if (read) {
			isPrivate = false;
		} else {
			isPrivate = true;
		}
		key = HttpUtils.urlEncode(key, true);
		if (isPrivate) {
			String signature = "";
			long expires = ((System.currentTimeMillis() / 1000) + expiration);
			try {
				signature = AuthUtils.calcSignature(auth.getAccessKeySecret(),
						bucket, key, overrides.getOverrides(),
						HttpMethod.GET.toString(), expires);
			} catch (SignatureException e) {
				e.printStackTrace();
				throw new Ks3ClientException("计算用户签名时出错", e);
			}
			if (overrides.getOverrides().size() > 0)
				return "http://" + bucket + "." + Constants.KS3_CDN_END_POINT
						+ "/" + key + "?AccessKeyId="
						+ URLEncoder.encode(auth.getAccessKeyId())
						+ "&Expires=" + expires + "&Signature="
						+ URLEncoder.encode(signature) + "&"
						+ HttpUtils.encodeParams(overrides.getOverrides());
			else
				return "http://" + bucket + "." + Constants.KS3_CDN_END_POINT
						+ "/" + key + "?AccessKeyId="
						+ URLEncoder.encode(auth.getAccessKeyId())
						+ "&Expires=" + expires + "&Signature="
						+ URLEncoder.encode(signature);
		} else {
			return "http://" + bucket + "." + Constants.KS3_CDN_END_POINT + "/"
					+ key + "?"
					+ HttpUtils.encodeParams(overrides.getOverrides());
		}
	}

	public HeadBucketResult headBucket(String bucketname)
			throws Ks3ClientException, Ks3ServiceException {
		return this.headBucket(new HeadBucketRequest(bucketname));
	}

	public HeadBucketResult headBucket(HeadBucketRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		return client.execute(auth, request, HeadBucketResponse.class);
	}

	public boolean bucketExists(String bucketname) throws Ks3ClientException,
			Ks3ServiceException {
		try {
			HeadBucketResult result = this.headBucket(bucketname);
			if (result.getStatueCode() == 404)
				return false;
			if (result.getStatueCode() == 200 || result.getStatueCode() == 301
					|| result.getStatueCode() == 403)
				return true;
			return false;
		} catch (Ks3ClientException e) {
			return false;
		}
	}

	public PutObjectResult putObject(String bucketname, String objectkey,
			File file) throws Ks3ClientException, Ks3ServiceException {
		return putObject(new PutObjectRequest(bucketname, objectkey, file));
	}

	public PutObjectResult putObject(String bucketname, String objectkey,
			InputStream inputstream, ObjectMetadata objectmeta)
			throws Ks3ClientException, Ks3ServiceException {
		return putObject(new PutObjectRequest(bucketname, objectkey,
				inputstream, objectmeta));
	}

	public PutObjectResult putObject(PutObjectRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		PutObjectResult obj = client.execute(auth, request,
				PutObjectResponse.class);
		return obj;
	}

	public CopyResult copyObject(String destinationBucket,
			String destinationObject, String sourceBucket, String sourceKey)
			throws Ks3ClientException, Ks3ServiceException {
		CopyObjectRequest request = new CopyObjectRequest(destinationBucket,
				destinationObject, sourceBucket, sourceKey);
		return this.copyObject(request);
	}

	public CopyResult copyObject(String destinationBucket,
			String destinationObject, String sourceBucket, String sourceKey,
			CannedAccessControlList cannedAcl) throws Ks3ClientException,
			Ks3ServiceException {
		CopyObjectRequest request = new CopyObjectRequest(destinationBucket,
				destinationObject, sourceBucket, sourceKey, cannedAcl);
		return this.copyObject(request);
	}

	public CopyResult copyObject(String destinationBucket,
			String destinationObject, String sourceBucket, String sourceKey,
			AccessControlList accessControlList) throws Ks3ClientException,
			Ks3ServiceException {
		CopyObjectRequest request = new CopyObjectRequest(destinationBucket,
				destinationObject, sourceBucket, sourceKey, accessControlList);
		return this.copyObject(request);
	}

	public CopyResult copyObject(CopyObjectRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		return client.execute(auth, request, CopyObjectResponse.class);
	}

	public HeadObjectResult headObject(String bucketname, String objectkey)
			throws Ks3ClientException, Ks3ServiceException {
		return headObject(new HeadObjectRequest(bucketname, objectkey));
	}

	public HeadObjectResult headObject(HeadObjectRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		return client.execute(auth, request, HeadObjectResponse.class);
	}

	public InitiateMultipartUploadResult initiateMultipartUpload(
			String bucketname, String objectkey) throws Ks3ClientException,
			Ks3ServiceException {
		return initiateMultipartUpload(new InitiateMultipartUploadRequest(
				bucketname, objectkey));
	}

	public InitiateMultipartUploadResult initiateMultipartUpload(
			InitiateMultipartUploadRequest request) throws Ks3ClientException,
			Ks3ServiceException {
		String objectkey = request.getObjectkey();
		InitiateMultipartUploadResult result = client.execute(auth, request,
				InitiateMultipartUploadResponse.class);
		result.setBucket(request.getBucketname());
		result.setKey(objectkey);
		return result;
	}

	public PartETag uploadPart(UploadPartRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		PartETag result = client.execute(auth, request,
				UploadPartResponse.class);
		result.setPartNumber(request.getPartNumber());
		return result;
	}

	public CopyResult copyPart(CopyPartRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		return client.execute(auth, request, CopyPartResponse.class);
	}

	public CompleteMultipartUploadResult completeMultipartUpload(
			String bucketname, String objectkey, String uploadId,
			List<PartETag> partETags) throws Ks3ClientException,
			Ks3ServiceException {
		return completeMultipartUpload(new CompleteMultipartUploadRequest(
				bucketname, objectkey, uploadId, partETags));
	}

	public CompleteMultipartUploadResult completeMultipartUpload(
			ListPartsResult result) throws Ks3ClientException,
			Ks3ServiceException {
		return completeMultipartUpload(new CompleteMultipartUploadRequest(
				result));
	}

	public CompleteMultipartUploadResult completeMultipartUpload(
			CompleteMultipartUploadRequest request) throws Ks3ClientException,
			Ks3ServiceException {
		return client.execute(auth, request,
				CompleteMultipartUploadResponse.class);
	}

	public void abortMultipartUpload(String bucketname, String objectkey,
			String uploadId) throws Ks3ClientException, Ks3ServiceException {
		this.abortMultipartUpload(new AbortMultipartUploadRequest(bucketname,
				objectkey, uploadId));
	}

	public void abortMultipartUpload(AbortMultipartUploadRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		this.client.execute(auth, request, AbortMultipartUploadResponse.class);
	}

	public ListPartsResult listParts(String bucketname, String objectkey,
			String uploadId) throws Ks3ClientException, Ks3ServiceException {
		ListPartsRequest request = new ListPartsRequest(bucketname, objectkey,
				uploadId);
		return listParts(request);
	}

	public ListPartsResult listParts(String bucketname, String objectkey,
			String uploadId, int maxParts) throws Ks3ClientException,
			Ks3ServiceException {
		ListPartsRequest request = new ListPartsRequest(bucketname, objectkey,
				uploadId);
		request.setMaxParts(maxParts);
		return listParts(request);
	}

	public ListPartsResult listParts(String bucketname, String objectkey,
			String uploadId, int maxParts, int partNumberMarker)
			throws Ks3ClientException, Ks3ServiceException {
		ListPartsRequest request = new ListPartsRequest(bucketname, objectkey,
				uploadId);
		request.setMaxParts(maxParts);
		request.setPartNumberMarker(partNumberMarker);
		return listParts(request);
	}

	public ListPartsResult listParts(ListPartsRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		return client.execute(auth, request, ListPartsResponse.class);
	}

	public void putObjectACL(String bucketName, String objectName,
			CannedAccessControlList accessControlList)
			throws Ks3ClientException, Ks3ServiceException {
		putObjectACL(new PutObjectACLRequest(bucketName, objectName,
				accessControlList));
	}

	public void putBucketACL(String bucketName,
			CannedAccessControlList CannedAcl) throws Ks3ClientException,
			Ks3ServiceException {
		this.putBucketACL(new PutBucketACLRequest(bucketName, CannedAcl));
	}

	public DeleteMultipleObjectsResult deleteObjects(
			DeleteMultipleObjectsRequest request) throws Ks3ClientException,
			Ks3ServiceException {
		return client.execute(auth, request,
				DeleteMultipleObjectsResponse.class);
	}

	public DeleteMultipleObjectsResult deleteObjects(List<String> keys,
			String bucketName) throws Ks3ClientException, Ks3ServiceException {
		return this.deleteObjects(new DeleteMultipleObjectsRequest(bucketName,
				keys));
	}

	public DeleteMultipleObjectsResult deleteObjects(String[] keys,
			String bucketName) throws Ks3ClientException, Ks3ServiceException {
		return this.deleteObjects(new DeleteMultipleObjectsRequest(bucketName,
				keys));
	}

	public ListMultipartUploadsResult listMultipartUploads(String bucketName)
			throws Ks3ClientException, Ks3ServiceException {
		ListMultipartUploadsRequest request = new ListMultipartUploadsRequest(
				bucketName);
		return this.listMultipartUploads(request);
	}

	public ListMultipartUploadsResult listMultipartUploads(String bucketName,
			String prefix) throws Ks3ClientException, Ks3ServiceException {
		ListMultipartUploadsRequest request = new ListMultipartUploadsRequest(
				bucketName, prefix);
		return this.listMultipartUploads(request);
	}

	public ListMultipartUploadsResult listMultipartUploads(String bucketName,
			String prefix, String keyMarker, String uploadIdMarker)
			throws Ks3ClientException, Ks3ServiceException {
		ListMultipartUploadsRequest request = new ListMultipartUploadsRequest(
				bucketName, prefix, keyMarker, uploadIdMarker);
		return this.listMultipartUploads(request);
	}

	public ListMultipartUploadsResult listMultipartUploads(
			ListMultipartUploadsRequest request) throws Ks3ClientException,
			Ks3ServiceException {
		return client
				.execute(auth, request, ListMultipartUploadsResponse.class);
	}

	public void putBucketCors(PutBucketCorsRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		client.execute(auth, request, PutBucketCorsResponse.class);
	}

	public BucketCorsConfiguration getBucketCors(String bucketname)
			throws Ks3ClientException, Ks3ServiceException {
		return getBucketCors(new GetBucketCorsRequest(bucketname));
	}

	public BucketCorsConfiguration getBucketCors(GetBucketCorsRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		return client.execute(auth, request, GetBucketCorsResponse.class);
	}

	public void deleteBucketCors(String bucketname) throws Ks3ClientException,
			Ks3ServiceException {
		deleteBucketCors(new DeleteBucketCorsRequest(bucketname));
	}

	public void deleteBucketCors(DeleteBucketCorsRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		client.execute(auth, request, DeleteBucketCorsResponse.class);
	}

	public <X extends Ks3WebServiceResponse<Y>, Y> Y execute(
			Ks3WebServiceRequest request, Class<X> clazz)
			throws Ks3ClientException, Ks3ServiceException {
		return client.execute(auth, request, clazz);
	}

	public PostObjectFormFields postObject(PostPolicy policy)
			throws Ks3ClientException {
		Map<String,Object> policyMap = new HashMap<String,Object>();
		policyMap.put("expiration", policy.getExpiration());
		
		List<List<String>> conditions = new ArrayList<List<String>>();
		for(PostPolicyCondition condition : policy.getConditions()){
			List<String> conditionList = new ArrayList<String>();
			if(condition.getMatchingType()!=MatchingType.contentLengthRange){
				if(!condition.getParamA().startsWith("$")){
					condition.setParamA("$"+condition.getParamA());
				}
			}else{	
				if(!StringUtils.checkLong(condition.getParamA())||!StringUtils.checkLong(condition.getParamB())){
					throw new ClientIllegalArgumentException("contentLengthRange匹配规则的参数A和参数B都应该是Long型");
				}
			}
			conditionList.add(condition.getMatchingType().toString());
			//表单中的项是忽略大小写的
			if(condition.getMatchingType()!=MatchingType.contentLengthRange&&!Constants.postFormUnIgnoreCase.contains(condition.getParamA().substring(1))){
				conditionList.add(condition.getParamA().toLowerCase());
			}else{
				conditionList.add(condition.getParamA());
			}
			conditionList.add(condition.getParamB());
			conditions.add(conditionList);
		}
		policyMap.put("conditions", conditions);
		String policyJson = StringUtils.object2json(policyMap);
		String policyBase64 = "";
		try {
			policyBase64 = new String(Base64.encodeBase64(policyJson.getBytes()),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			//unexpected
		}
		PostObjectFormFields fields = new PostObjectFormFields();
		fields.setKssAccessKeyId(auth.getAccessKeyId());
		fields.setPolicy(policyBase64);
		try {
			fields.setSignature(AuthUtils.calcSignature(auth.getAccessKeySecret(), policyBase64));
		} catch (SignatureException e) {
			throw new Ks3ClientException("计算签名出错",e);
		}
		return fields;
	}
	
	public PostObjectFormFields postObject(String bucket,String filename,
			Map<String, String> postFormData,List<String> unknowValueFormFiled) throws Ks3ClientException {
		if(StringUtils.isBlank(bucket))
			throw ClientIllegalArgumentExceptionGenerator.notNull("bucket");
		if(postFormData==null)
			postFormData = new HashMap<String,String>();
		if(unknowValueFormFiled==null)
			unknowValueFormFiled = new ArrayList<String>();
		postFormData.put("bucket",bucket);
		PostPolicy policy = new PostPolicy();
		//签名将在五小时后过期
		policy.setExpiration(DateUtils.convertDate2Str(new DateTime().plusHours(5).toDate(),DATETIME_PROTOCOL.ISO8861));
		
		for(Entry<String,String> entry:postFormData.entrySet()){
			if(!Constants.postFormIgnoreFields.contains(entry.getKey())){
				PostPolicyCondition condition = new PostPolicyCondition();
				condition.setMatchingType(MatchingType.eq);
				condition.setParamA("$"+entry.getKey());
				condition.setParamB(entry.getValue().replace("${filename}", filename));
				policy.getConditions().add(condition);
			}
		}
		for(String field:unknowValueFormFiled){
			if(!Constants.postFormIgnoreFields.contains(field)){
				PostPolicyCondition condition = new PostPolicyCondition();
				condition.setMatchingType(MatchingType.startsWith);
				condition.setParamA("$"+field);
				condition.setParamB("");
				policy.getConditions().add(condition);
			}
		}
		return postObject(policy);
	}

	public String putAdpTask(String bucketName, String objectKey,
			List<Adp> adps) throws Ks3ClientException, Ks3ServiceException {
		PutAdpRequest request = new PutAdpRequest(bucketName,objectKey,adps);
		return putAdpTask(request);
	}

	public String putAdpTask(String bucketName, String objectKey,
			List<Adp> adps, String notifyURL) throws Ks3ClientException,
			Ks3ServiceException {
		PutAdpRequest request = new PutAdpRequest(bucketName,objectKey,adps);
		request.setNotifyURL(notifyURL);
		return putAdpTask(request);
	}

	public String putAdpTask(PutAdpRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		return client.execute(auth, request, PutAdpResponse.class);
	}

	public AdpTask getAdpTask(String taskid) throws Ks3ClientException,
			Ks3ServiceException {
		GetAdpRequest request = new GetAdpRequest(taskid);
		return getAdpTask(request);
	}

	public AdpTask getAdpTask(GetAdpRequest request)
			throws Ks3ClientException, Ks3ServiceException {
		return client.execute(auth, request, GetAdpResponse.class);
	}
}
