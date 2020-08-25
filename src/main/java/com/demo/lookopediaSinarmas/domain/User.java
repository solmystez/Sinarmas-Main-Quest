package com.demo.lookopediaSinarmas.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User implements UserDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Email(message = "Needs to be an email !")
	@NotBlank(message = "Email is required !")
	@Column(unique = true)
	private String email;
	
	@NotBlank(message = "Username is required !")
	private String username;
	
	@NotBlank(message = "Password is required !")
	private String password;
	
	@Transient //for match with pw, before persist, once persist not persist anymore
	private String confirmPassword;
	
	private boolean hasMerchant;
	
	private String orderNow;
	private Integer invoiceSequence = 1;
	
	private Date created_At;
	private Date updated_At;
	
	@OneToMany(fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			mappedBy = "user")
	@JsonIgnore
	private Set<Order> order = new HashSet<>();
	
	@OneToOne(fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			mappedBy = "userMerchant")
	@JsonIgnore
	private Merchant merchant;
	
	
	@OneToMany(fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			mappedBy = "userAddress")
	@JsonIgnore
	private List<Address> address = new ArrayList<>();
	
	public User() {

	}
	
//	public void setInvoiceToList(Invoice invoice) {
//		List<Invoice> invoices = null;
//			
//		if(this.getInvoice() == null) new ArrayList<>();
//		else this.getInvoice();
//		
//		invoice.setUser(this);
//		invoices.add(invoice);
//		this.setInvoice(invoices);
//	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getInvoiceSequence() {
		return invoiceSequence;
	}

	public void setInvoiceSequence(Integer invoiceSequence) {
		this.invoiceSequence = invoiceSequence;
	}

	public String getOrderNow() {
		return orderNow;
	}

	public void setOrderNow(String orderNow) {
		this.orderNow = orderNow;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public Date getCreated_At() {
		return created_At;
	}

	public void setCreated_At(Date created_At) {
		this.created_At = created_At;
	}

	public Date getUpdated_At() {
		return updated_At;
	}

	public void setUpdated_At(Date updated_At) {
		this.updated_At = updated_At;
	}

	public Set<Order> getOrder() {
		return order;
	}

	public void setOrder(Set<Order> order) {
		this.order = order;
	}

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}
	
	
	public boolean isHasMerchant() {
		return hasMerchant;
	}

	public void setHasMerchant(boolean hasMerchant) {
		this.hasMerchant = hasMerchant;
	}

	@PrePersist
	protected void onCreate() {
		this.created_At = new Date();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updated_At = new Date();
	}
	
	

	//	user details interface method
	//json ignore for, we want get rid of this from postman result
	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {//we don't play role
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {//case : the account expired because didn't pay
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {//the account not locked
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {//make return true this too 
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	@JsonIgnore
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}




}
