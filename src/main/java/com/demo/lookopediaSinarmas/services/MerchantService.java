package com.demo.lookopediaSinarmas.services;

import java.nio.file.Paths;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.demo.lookopediaSinarmas.controller.MerchantController;
import com.demo.lookopediaSinarmas.entity.Cart;
import com.demo.lookopediaSinarmas.entity.Merchant;
import com.demo.lookopediaSinarmas.entity.Orders;
import com.demo.lookopediaSinarmas.entity.User;
import com.demo.lookopediaSinarmas.exceptions.merchant.MerchantNameAlreadyExistsException;
import com.demo.lookopediaSinarmas.exceptions.order.OrderNotFoundException;
import com.demo.lookopediaSinarmas.exceptions.product.ProductNotFoundException;
import com.demo.lookopediaSinarmas.exceptions.user.UserIdNotFoundException;
import com.demo.lookopediaSinarmas.repositories.CartRepository;
import com.demo.lookopediaSinarmas.repositories.MerchantRepository;
import com.demo.lookopediaSinarmas.repositories.OrderRepository;
import com.demo.lookopediaSinarmas.repositories.UserRepository;
import com.demo.lookopediaSinarmas.services.image.ImageStorageService;


@Service
public class MerchantService {
	
	private static Logger log = LoggerFactory.getLogger(MerchantController.class);
	
	@Autowired
	MerchantRepository merchantRepository;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	CartRepository cartRepository;
	
	@Autowired
	private ImageStorageService imageStorageService;

	public Merchant createMerchant(Long user_id, Merchant merchant, String username) {
		User user;
		try {
			user = userRepository.findById(user_id).get();
		} catch (Exception e1) {
			throw new UserIdNotFoundException("User not found");
		}
		if(user.isHasMerchant()) throw new UserIdNotFoundException("User already be a merchant !");		
		try {
			if(!user.getUsername().equals(username)) {
				throw new UserIdNotFoundException("cannot create merchant, wrong user_id parameter");
			}
			user.setHasMerchant(true);
//			user.setMerchant(merchant);
			merchant.setUserMerchant(user);
			merchant.setUsername(user.getUsername());
			merchant.setMerchantName(merchant.getMerchantName());
			merchant.setMerchantAddress(merchant.getMerchantAddress());
			log.info("merchant created");
			return merchantRepository.save(merchant);
		} catch (Exception e) {
			throw new MerchantNameAlreadyExistsException("Merchant name already used !");
		}
	}
	
	public Merchant updateMerchant(Long user_id, Merchant merchant, String username, MultipartFile file) {
		
		User user;
		try {
			user = userRepository.findById(user_id).get();
		} catch (Exception e1) {
			throw new UserIdNotFoundException("User not found");
		}
		if(user.isHasMerchant()) throw new UserIdNotFoundException("User already be a merchant !");
		if(!user.getUsername().equals(username)) {
			throw new UserIdNotFoundException("cannot create merchant, wrong user_id parameter");
		}
		
		try {
			String fileName = null;
			if(!file.isEmpty()) fileName = imageStorageService.storeFile(file);
			else fileName = "nophoto.jpg";
			
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/api/merchant/loadImageMerchant/")
					.path(fileName)
					.toUriString();
			
			String merchantFileName = file.getOriginalFilename();
			String merchantFileType = file.getContentType();
			long size = file.getSize();
			String merchantFileSize = String.valueOf(size);
			merchant.setFileName(merchantFileName);
			merchant.setFilePath(fileDownloadUri);
			merchant.setFileType(merchantFileType);
			merchant.setFileSize(merchantFileSize);
			
			if(merchant.getId() != null) {
				user.setMerchant(merchant);
				merchant.setUsername(user.getUsername());
				merchant.setUserMerchant(user);
				log.info("merchant updated");
			}
			return merchantRepository.save(merchant);

		} catch (Exception e) {
			throw new MerchantNameAlreadyExistsException("Merchant name already used !");
		}
		
	}
	
	public Merchant findMerchantByUserId(Long id) {
		Merchant merchant;
		try {
			merchant = merchantRepository.findMerchantByUserMerchantId(id);
		} catch (Exception e) {
			throw new UserIdNotFoundException("User Id '" + id + "' doesn't exist");
		}
		
		return merchant;
	}

	public Iterable<Orders> findAllIncomingOrder(String merchant_name) {
		List<Orders> order = null;
		try {
			order = orderRepository.findAllByMerchantName(merchant_name);
		} catch (Exception e) {
			throw new OrderNotFoundException("No order yet");
		}
		
		return order;
	}

	public Orders accOrRejectProductOrder(Orders order, Long order_id) {
		
		Orders order1 = orderRepository.findById(order_id).get();
		if(order.getStatus().equals("Accept")) order1.setStatus("Process");
		else if(order.getStatus().equals("Reject")) order1.setStatus("Rejected");
		
		
		return orderRepository.save(order1);
	}

	public Orders userFinishOrder(@Valid Orders order, Long order_id) {
		
		Orders order1 = orderRepository.findById(order_id).get();
		if(order.getStatus().equals("Finish")) {
			order1.setStatus("Finish");
			order1.setHasRating("Done");
		}
		
		return orderRepository.save(order1);
	}
	

}
