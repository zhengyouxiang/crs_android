package com.tool.client;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key; 
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RsaHelper {
	
	private static final String KEY_ALGORITHM = "RSA";
	private static final String PUBLIC_KEY = "publicKey";
	private static final String PRIVATE_KEY = "privateKey";
	
	private static final int BIT_LENGTH = 1024;
	
	private static String publicKeyString;
	
	private static Map<String, String> keyMap;
	
	public static void genKey() {
		keyMap = new HashMap<String, String>();
		KeyPairGenerator keyPairGenerator;
		try {
			
			keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
			SecureRandom random = new SecureRandom();
			keyPairGenerator.initialize(BIT_LENGTH, random);
			
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			
			RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
			keyMap.put(PUBLIC_KEY, getKeyString(publicKey));
			
			RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
			keyMap.put(PRIVATE_KEY, getKeyString(privateKey));
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getPublicKeyString() {
		return keyMap.get(PUBLIC_KEY);
	}
	
	public static String getPublicKeyModulus() throws Exception {
		RSAPublicKey rsaPublicKey = getPublicKey(getPublicKeyString());
		return rsaPublicKey.getModulus().toString();
	}
	
	public static String getPrivateKeyString() {
		return keyMap.get(PRIVATE_KEY);
	}
	
	private static RSAPublicKey getPublicKey(String publicKey) throws Exception {
		byte[] keyBytes = getKeyBytes(publicKey);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(spec);
		return key;
	}
	
	private static RSAPrivateKey getPrivateKey(String privateKey) throws Exception {
		byte[] keyBytes = getKeyBytes(privateKey);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(spec);
		return key;
	}
	
	public static void setPublicKey(String publicKey) throws Exception {
		BigInteger modulus = new BigInteger(publicKey);
		BigInteger publicExponent = new BigInteger("65537");
		RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExponent);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(spec);
		publicKeyString = getKeyString(key);
	}
	
	public static String encode(String str) {
		return encode(str, publicKeyString);
	}
	
	public static String encode(String str, String publicKey)  {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
			byte[] plainText = str.getBytes();
			RSAPublicKey rsaPublicKey = getPublicKey(publicKey);
			cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
			byte[] enBytes = cipher.doFinal(plainText);
			return Base64Helper.encode(enBytes);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
		
	}
	
	public static String decode(String enBytes) throws Exception {
		String privateKey = getPrivateKeyString();
		return decode(enBytes, privateKey);
	}
	
	public static String decode(String enBytes, String privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
		RSAPrivateKey rsaPrivateKey = getPrivateKey(privateKey);
		cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
		byte[] deBytes = cipher.doFinal(Base64Helper.decode(enBytes));
		return (new String(deBytes));
	}
	
	private static byte[] getKeyBytes(String keyString) throws Exception {
		byte[] keyBytes = Base64Helper.decode(keyString);
		return keyBytes;
	}
	
	private static String getKeyString(Key key) throws Exception {
		byte[] keyBytes = key.getEncoded();
		String s = Base64Helper.encode(keyBytes);
		return s;
	}
	
}
