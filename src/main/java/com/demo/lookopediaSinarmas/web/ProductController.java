package com.demo.lookopediaSinarmas.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.lookopediaSinarmas.domain.Category;
import com.demo.lookopediaSinarmas.domain.Orders;
import com.demo.lookopediaSinarmas.domain.Product;
import com.demo.lookopediaSinarmas.domain.User;
import com.demo.lookopediaSinarmas.services.CategoryService;
import com.demo.lookopediaSinarmas.services.CommentService;
import com.demo.lookopediaSinarmas.services.ProductService;
import com.demo.lookopediaSinarmas.services.RatingService;
import com.demo.lookopediaSinarmas.services.otherService.MapValidationErrorService;

@CrossOrigin
@RestController
@RequestMapping("/api/product")
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private RatingService ratingService;
	
	@Autowired
	private MapValidationErrorService mapValidationErrorService;

	@GetMapping("/findProductByCategory/{category_name}")
	public Iterable<Product> loadMerchantProduct(@PathVariable String category_name){
		return productService.findProductByCategory(category_name);
	}
	
	@GetMapping("/findProduct/{product_id}")
	public ResponseEntity<?> findSpecificProduct(@PathVariable Long product_id) {
		
		Product product = productService.findProductById(product_id);
		
		return new ResponseEntity<Product>(product, HttpStatus.OK);
	}
	
	@GetMapping("/loadAllProductOnCatalog")
	public Iterable<Product> loadAllProduct(){
		return productService.findAllProducts();
	}
	
	
	@DeleteMapping("/deleteProduct/{product_id}")
	public ResponseEntity<?> deleteProduct(@PathVariable Long product_id){
		
		productService.deleteProductById(product_id);

		return new ResponseEntity<String>("Product ID '" + product_id  + "' was successfully deleted", HttpStatus.OK);
	}
}
