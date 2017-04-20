package com.ibm.websphere.samples.pbw.bean;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.websphere.samples.pbw.jpa.Inventory;
import com.ibm.websphere.samples.pbw.war.AccountBean;

@Path("/cart")
public class ShoppingCartResource {

	@Inject
	private ShoppingCartBean cart;
	@Inject
	private AccountBean account;
	@Inject
	private CatalogMgr catalog;
	@Inject
	private CustomerMgr customer;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public int getShoppingCartSize() {
		return cart.getSize();
	}
	
	@GET @Path("/items")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Inventory> getShoppingCartItems() {
		return cart.getItems();
	}
	
	@GET @Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStatus() {
		if (account.isRegister()) {
			return account.getCustomer().getFirstName();
		} else {
			return "nope";
		}
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public Response performLogin(@FormParam("email") String email, @FormParam("password") String password) throws URISyntaxException {
		System.out.println("Email: " + email + ", Password: " + password);
		
		// initialize login info and set email/password
		account.performLogin();
		account.getLoginInfo().setEmail(email);
		account.getLoginInfo().setPassword(password);
		
		// perform login and check status
		String status = account.performLoginComplete();
		System.out.println("Status = " + status);
		
		// always redirect to checkout page
		java.net.URI location;
		location = new java.net.URI("http://localhost:9080/PlantsByWebSphere/register.html");
		System.out.println("Redirecting to " + location);
		return Response.temporaryRedirect(location).build();
		
		// TODO once create account works, fix this
		
		// check for status and return
		/*if (status == null) {
			java.net.URI location;
			
			try {
				location = new java.net.URI("checkout.html");
				return Response.temporaryRedirect(location).build();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		    return Response.status(-1).build();
		} else {
			return Response.status(0).build();
		}*/
	}
	
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createCustomer(@FormParam("email") String email, @FormParam("passwrord") String password, @FormParam("firstname") String firstName, @FormParam("lastname") String lastName, 
			@FormParam("address1") String addr1, @FormParam("address2") String addr2,  @FormParam("city") String city, 
			@FormParam("state") String state, @FormParam("zipCode") String zipCode, @FormParam("phonenum") String phonenum) throws URISyntaxException {
			
		
		account.performRegister();
		
		// Updates the empty new Customer object created by performRegister.
		account.getCustomer().setCustomerID(email);
		account.getCustomer().setPassword(password);
		account.getCustomer().setFirstName(firstName);
		account.getCustomer().setLastName(lastName);
		account.getCustomer().setAddr1(addr1);
		account.getCustomer().setAddr2(addr2);
		account.getCustomer().setAddrCity(city);
		account.getCustomer().setAddrState(state);
		account.getCustomer().setAddrZip(zipCode);
		account.getCustomer().setPhone(phonenum);
		
		// Sets the values for the new Customer.
		account.performAccountUpdate();
		
		// Redirects user to checkout page.
		java.net.URI location;
		location = new java.net.URI("http://localhost:9080/PlantsByWebSphere/checkout.html");
		System.out.println("Redirecting to " + location);
		return Response.temporaryRedirect(location).build();

	}
	
	@POST
	@Path("/checkout")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public boolean performCheckout(String data) {
		System.out.println(data);

		// TODO finish checkout process
		return true;
	}
	
	@POST
	@Path("{id}")
	public void addItemToCart(@PathParam("id") String id) {
		cart.addItem(catalog.getItemInventory(id));
	}
	
	@GET @Path("/productinfo/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Inventory getProductInfo(@PathParam("id") String id) {
		return catalog.getItemInventory(id);
	}
	
}
