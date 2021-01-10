package com.demo.lookopediaSinarmas.services;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.lookopediaSinarmas.entity.Orders;
import com.demo.lookopediaSinarmas.entity.Voucher;
import com.demo.lookopediaSinarmas.exceptions.product.ProductIdException;
import com.demo.lookopediaSinarmas.exceptions.voucher.VoucherErrorException;
import com.demo.lookopediaSinarmas.repositories.OrderRepository;
import com.demo.lookopediaSinarmas.repositories.VoucherRepository;

@Service
public class VoucherService {
	
	@Autowired
	VoucherRepository voucherRepository;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	CartService cartService;
	
	//apply discount to cart, get total_p from order by order ident
	
	public Voucher createOrUpdateVoucher(Voucher voucher) {
		
		try {
			voucher.setVoucherCode(voucher.getVoucherCode());
			voucher.setVoucherName(voucher.getVoucherName());
			voucher.setVoucherDiscount(voucher.getVoucherDiscount());
			voucher.setVoucherDescription(voucher.getVoucherDescription());
			
			return voucherRepository.save(voucher);
		} catch (Exception e) {
			throw new VoucherErrorException("something wrong while create voucher");
		}
	}

	public Iterable<Voucher> getVoucherList(){
		return voucherRepository.findAll();
	}

	public void deleteVoucherById(Long voucher_id) {
		try {
			Voucher voucher = voucherRepository.findById(voucher_id).get();
			voucherRepository.delete(voucher);	
		} catch (Exception e) {
			throw new ProductIdException("Voucher with ID '" + voucher_id +"' cannot delete because doesn't exists");			
		}
	}

	public List<Orders> applyVoucher(Voucher voucher, Long user_id) {
		
		Voucher voucher1 = voucherRepository.findByVoucherCode(voucher.getVoucherCode());
		
		double discount = voucher1.getVoucherDiscount();
		double temp = 0;
		double totalPrice = 0;
		String status = "Not Paid";
		List<Orders> order = orderRepository.findAllByUserIdAndStatus(user_id, status);
		
		for(int i=0 ;i<order.size(); i++) {
			temp = 0;
			temp = order.get(i).getTotal_price() * (discount/100);
			totalPrice = order.get(i).getTotal_price() - temp;
			order.get(i).setTotal_price(totalPrice);
			order.get(i).setHasVoucher("Yes");
			orderRepository.save(order.get(i));
		}
		return order;
	}
	
	public List<Orders> cancelApplyVoucher(Long user_id, String username) {
		String status = "Not Paid";
		List<Orders> order = orderRepository.findAllByUserIdAndStatus(user_id, status);
		cartService.countOrderPriceAndStock(username);
		for(int i=0 ;i<order.size(); i++) {
			order.get(i).setHasVoucher("No");
			orderRepository.save(order.get(i));
		}
		
		return order;
	}

	public String returnStatusVoucher(Long user_id) {
		
		String status = "Not Paid";
		String temp = "";
		List<Orders> orders = orderRepository.findAllByUserIdAndStatus(user_id, status);
		for(int i=0; i<orders.size(); i++) {
			temp = orders.get(i).getHasVoucher();
			break;
		}
		return temp;
	}
}
