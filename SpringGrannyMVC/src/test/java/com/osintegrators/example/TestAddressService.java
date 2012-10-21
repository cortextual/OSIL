/**
 * 
 */
package com.osintegrators.example;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author acoliver
 *
 */ 
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class TestAddressService {
    @PersistenceContext
    EntityManager em;

	@Autowired
	AddressService addressService;

    @BeforeTransaction
    public void verifyDatabaseStateBeforeTransaction() {
        verifyNoDataInAddressTable();
    }

    @AfterTransaction
    public void verifyDatabaseStateAfterTransaction() {
        verifyNoDataInAddressTable();
    }

    public void verifyNoDataInAddressTable() {
        List<Address> allAddresses = addressService.getAllAddresses();
        Assert.assertEquals("there should not be any addresses in the database", 0, allAddresses.size());
    }

	/** 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
    @Rollback
	public void testCreateAddress() {
		String expectedName = "John Doe";
		String expectedAddress = "345 West Main St\nDurham, NC";
		String expectedPhone = "+1.919.321.0119";
		String expectedEmail = "spam@osintegrators.com";
		Address address = createAddressObject(expectedName, expectedAddress, expectedPhone,expectedEmail);
		addressService.createAddress(address);
		Address result = addressService.getAddressByName(expectedName);
		
		assertEquals(expectedName, result.getName());
		assertEquals(expectedAddress,result.getAddress());
		assertEquals(expectedPhone,result.getPhone());
		assertEquals(expectedEmail,result.getEmail());

        em.flush();
    }
	
	@Test
    @Rollback
	public void testGetAllAddresses() {
		String[] expectedName = {"John Doe", "Sarah Palin", "John Edwards", "Joe Lieberman", "Jack Kemp", "Dan Quayle"};
        String[] expectedAddress = {"345 West Main St\nDurham, NC",
                "Overlooking Russia\nJuneau",
                "123 not at my house\nRaleigh, NC",
                "321 Not at my Party,\nHartford, CT",
                "545 Soccer is for Socialists,\nNY, NY",
                "62 You Say Tomato, I say Tomatoe,\nIndianapolis, IN"};
		String[] expectedPhone = {"+1.919.321.0119", "999-9999", "199-9999", "991-9999", "999-9998", "113-1548"};
		String[] expectedEmail = {"spam@osintegrators.com", "sarah@gop.org", "john@nowhere.com",
                "joe@nowhere.com", "jack@gop.org", "dan@gop.org"};
		
		for (int i = 0; i < expectedName.length; i++) {
			Address address = createAddressObject(expectedName[i], expectedAddress[i], expectedPhone[i],
                    expectedEmail[i]);
			addressService.createAddress(address);
		}
		
		List<Address> addresses = addressService.getAllAddresses();
		
		for (int i = 0; i < addresses.size(); i++) {
			Address address = addressService.getAddressByName(expectedName[i]);
			assertEquals(expectedName[i], address.getName());
			assertEquals(expectedAddress[i],address.getAddress());
			assertEquals(expectedPhone[i],address.getPhone());
			assertEquals(expectedEmail[i],address.getEmail());
		}
		assertEquals("there should be equal number of addresses as there are expectedName array entries",
                expectedName.length, addresses.size());
	}
	
	@Test
    @Rollback
	public void testDeleteAddress() {
		String expectedName = "Lloyd Bentsen";
		String expectedAddress = "123 Mission way\nHidalgo, TX";
		String expectedPhone = "+1.999.999.9999";
		String expectedEmail = "lloyd@dnc.org";
		Address address = createAddressObject(expectedName, expectedAddress, expectedPhone,expectedEmail);
		addressService.createAddress(address);
		Address result = addressService.getAddressByName(expectedName);
		addressService.deleteAddress(result);
		result = addressService.getAddressByName(expectedName);
		assertNull("there should be no Lloyd Bentsen after the delete", result);

        em.flush();
	}

	private Address createAddressObject(String name, String address,
			String phone, String email) {
		Address entity = new Address(name,address,phone,email);
		return entity;
	}

}
