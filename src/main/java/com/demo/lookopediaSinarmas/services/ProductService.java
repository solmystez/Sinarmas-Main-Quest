package com.demo.lookopediaSinarmas.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.demo.lookopediaSinarmas.domain.Merchant;
import com.demo.lookopediaSinarmas.domain.Product;
import com.demo.lookopediaSinarmas.exceptions.merchant.MerchantNotFoundException;
import com.demo.lookopediaSinarmas.exceptions.product.ProductIdException;
import com.demo.lookopediaSinarmas.exceptions.product.ProductNotFoundException;
import com.demo.lookopediaSinarmas.repositories.MerchantRepository;
import com.demo.lookopediaSinarmas.repositories.ProductRepository;

@Service
public class ProductService {
	
	public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";


	@Autowired
	ProductRepository productRepository;

	@Autowired
	MerchantRepository merchantRepository;

	public Product createProduct(Long merchant_id, Product product, String merchantName) {

		try {
		    Merchant merchant = merchantRepository.findMerchantByUserMerchantUsername(merchantName);
		    
		    //here the progress logic i made it now from some website tutorial
//		    String upImg = uploadImage(product.getProductImagePicture());
//		    product.setProductImagePicture(upImg);
		    
		    product.setMerchant(merchant);
		    product.setMerchantName(merchant.getMerchantName());
//		    product.setProductCategory(product.getProductCategory().toLowerCase());
		    
		    Integer totalProduct = merchant.getTotalProduct();
		    totalProduct++;		    
		    merchant.setTotalProduct(totalProduct);
		    		
			return productRepository.save(product);
		} catch (Exception e) {
			throw new MerchantNotFoundException("Merchant not found");
		}
	}
	
	
	public Product updateProduct(Long merchantId, Product product, String merchantName) {
	
		//prevent update product that doesn't belong to this merchant
		if(product.getProduct_id() != null) {
			Product existingProduct = productRepository.findById(product.getProduct_id()).get();
			
			if(existingProduct != null && (!existingProduct.getMerchantName().equals(merchantName))) {
				throw new ProductNotFoundException("Product not found in your merchant");
			}else if (existingProduct == null) {
				throw new ProductNotFoundException("Product '" + product.getProductName() + "' cannot updated, because it doesn't exist");
			}
		}

		
		try {
		    Merchant merchant = merchantRepository.findById(merchantId).get();
		    	    
			product.setMerchant(merchant);
//			product.setProductCategory(product.getProductCategory().toLowerCase());
			
			return productRepository.save(product);
		} catch (Exception e) {
			throw new MerchantNotFoundException("merchant not found");
		}

	}
	
	//for findProduct
	//if (!product.getMerchantName.equal(merchant name)) throw new exception

	
	public Iterable<Product> findAllProducts() {
		return productRepository.findAll(); 
	}
	
	public List<Product> findAllProductsByMerchantId(Long id) {
		return productRepository.findAllProductsByMerchantId(id); 
	}
	
	public List<Product> findProductByCategory(String categoryName) {
		return productRepository.findProductsByProductCategory(categoryName.toLowerCase()); 
	}
	
	public Product findProductById(Long product_id) {
		
		Product product;
		
		try {
			product = productRepository.findById(product_id).get();
	
		} catch (Exception e) {
			throw new ProductIdException("Product with ID '" + product_id +"' cannot delete because doesn't exists");			
		}
		
		return product;
	}
	

	public void deleteProductById(Long product_id) {
		
		try {
			Product product = productRepository.findById(product_id).get();
			productRepository.delete(product);	
		} catch (Exception e) {
			throw new ProductIdException("Product with ID '" + product_id +"' cannot delete because doesn't exists");			
		}
	}
	
	
	//upload image to base64 string
	private String uploadImage(String imageVal) throws Exception {
		
			byte[] decodedBytes = Base64.getDecoder().decode(imageVal);
			String decodedString = new String(decodedBytes);
			
			
//			FileInputStream imageStream = new FileInputStream(imageVal);
//			byte[] imageByte = imageStream.readAllBytes();
//			String imageString = Base64.getEncoder().encodeToString(imageByte);
			
//			byte[] imageByte2 = org.apache.tomcat.util.codec.binary.Base64.decodeBase64(imageVal);
	
			//save file local
			String filePath = Paths.get(uploadDirectory).toString();
			FileWriter fileWriter = new FileWriter(filePath);
			fileWriter.write(decodedString);
			fileWriter.close();
//			imageStream.close();
			
//			new FileOutputStream(filePath).write(decodedString);
			
//			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
//			stream.write(filePath.getBytes());
//			stream.close();
//			
			return decodedString;
	}
	
}
