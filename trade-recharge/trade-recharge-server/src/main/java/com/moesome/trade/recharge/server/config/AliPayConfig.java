package com.moesome.trade.recharge.server.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliPayConfig {
	public static final String URL = "https://openapi.alipaydev.com/gateway.do";
	public static final String APP_ID = "2016092800619165";
	public static final String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCMwk1wegdDH6gZ/AFbRtJ0JfGyY3K8mm40eOgTLavhzpV0cInju4RJGUz1CN9wXn7zyZWd/VlrMh78tL+TR4+CbHfYdTPZL/98+tMRpO0bn//S1nSAtu1A9BsJ4wxo9YuJEtavNwpWoEVGoTCxNmgs9+FvxDdIElsFbJ5RAvYC7ZcgRNB7vPOvR+p6gRLJPTLTC5pyOzG1E1nDMzsvlEOJtx7ltW40Y/fbnU/laxJj9G1rWDHQAw1T024AFi4vayBeG5TXEdmEPT8zdEStGWGOKKbj+PD7Fz3OC4vSNwAk+to//uoS2PP5xYPqMFv4/gyUKkgQSQGaslUJyQe6THG9AgMBAAECggEAcj2IqE8B8eKRhnVXjINQb2AUaBn6l0QnSYMPp/EbXWtTNWvZHfGlDcbxBLas0gB/vNDRE6b5mdzV9XelPJ2JxerO4elNqWohS4gvIlTe89rJwI65g3Xz3RHBmCakfdM76oZVo2NiiqgNUaOlZ06nAkg0uYdg7qenJCHh5BIpJHoFcDBnT7VeqgvsMjTJPOcNXQKnVmWJHhk6mY1yINL6iKUTBQUgNIBgy2YMlMl4iBJafKDtngYPWiDWZ+DndDYSVJHiqdJVDCcL6Cas52AOq0sES9mwSGnhWDB4ZU+GqrIIV5YqOPeDtNGZMFxnRKhROP03iATxXYLF8kVpkLVqAQKBgQDZyRNoYsDI609rGKMQGDtU5z3qov+ZSqFjGSEwwiSDPGbTdx/jVvhHMcroLPwRaraygOU3F9fFIRw+PTVTavAtUGNmLaJ6kR45lyVBn171G3iUXq5qC29nUhrWfS/DRF02+48mZdCV8bqQYpf0a4y5L9xkh1nmJpmFI9M2K45kfQKBgQCldTKVWShMxFLzYg4VtDsGJbZt0bFcUEo+a+MbRX5T7Rhokat0Y+uNzK7HqE9d6d+9juZ6n5YOH9Ql8vQIUkWIakVqrjlTu7Rgak8fUxZtbtbFM5Y5ayUmCDCo5/ye3iEBsu3zZ7fTDS+z7A//gtFVshuolIL7mWzljSyelBIGQQKBgQCU4Lkstb12XC3ZV2k5Omds3ftcp/q0zujOdsJSs5UERclBLAEXhz+IY8iCuSXDvkCQfD61T85Hyx+kiHEoykTPpVGd6vpUcVnJIsLsPkdKpXHjc2olwhVw+xavo7p/8P8L9CHirN1Pc4UG6O9Zvh/gBJDUBBb3l1GBS4E6WcIofQKBgH8dsUUIgGuqEUinNsb59rbcVnG8vpR7ou6eGJclJs73wt7ju2PqOFhyvnzqFD/EpH6PwQbIgJJNZcHaDB4whvctHjO9spFgDk5cnnkkboCYvSw4W9u1M48qj6fUw0KSyazyeNmd/56H7PRAEnJtYMKD2bgEkTUXRJB/i54xiD+BAoGBALykBa7KlGHUc4Z5PfkJSY46Qj8OTCouYnxV2xuJ9DIBPhENfey+pqEbpOvDXw571dT4wk0iVXoGLin1xG2zM7K+Jx5f8pN5LFUPzRgUytpKzO8emdH9N9eJ6Qy9bYrJMhwepoBHSdzzZr4uAvocCykDeaKNV+vH/yWsv1zWTRaS";
	public static final String FORMAT = "json";
	public static final String CHARSET = "UTF-8";
	public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxD8nPQuVsMNvdyre/3tseGucbn/qofj3vUxm5COIm+6dJhGQWNwOqsrZ+7mOOKELtYGlybv57HJWfdTpQKZYSCAkfSPLTngTZCA8AA/gcCN+pvRiI/mEGVAJTrsMvNv/FvCcqDaC0NXN3wshdCXiuwpX33dH+MqixNHIlSO+XOUFWkryHG1DHpmdq422J9GOOYfKxfWNN2+arDkMIXAegUvHm/uOxsLWACLlKfWho3vAAmmGCZTl4szYNO4jH3SBEqpagOV3lADwNijcFZZp/0wLzLFPOKrwd4Ux53rJW7idyEJOnpvSbcTRGkIyy/o1aAX1h1nWFx3viRp7v2OKHQIDAQAB";
	public static final String SIGN_TYPE = "RSA2";

	@Bean
	public AlipayClient alipayClient(){
		return new DefaultAlipayClient(URL,APP_ID,APP_PRIVATE_KEY,FORMAT,CHARSET,ALIPAY_PUBLIC_KEY,SIGN_TYPE);
	}
}
