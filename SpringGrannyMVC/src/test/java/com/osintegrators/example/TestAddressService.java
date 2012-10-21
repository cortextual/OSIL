/**
 * 
 */
package com.osintegrators.example;

import static org.junit.Assert.*;

import java.util.List;

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

    // test data
    private String[] testNames = {"John Doe", "Sarah Palin", "John Edwards", "Joe Lieberman", "Jack Kemp", "Dan Quayle"};
    private String[] testAddresses = {"345 West Main St\nDurham, NC",
            "Overlooking Russia\nJuneau",
            "123 not at my house\nRaleigh, NC",
            "321 Not at my Party,\nHartford, CT",
            "545 Soccer is for Socialists,\nNY, NY",
            "62 You Say Tomato, I say Tomatoe,\nIndianapolis, IN"};
    private String[] testPhoneNumbers = {"+1.919.321.0119", "999-9999", "199-9999", "991-9999", "999-9998", "113-1548"};
    private String[] testEmailAddresses = {"spam@osintegrators.com", "sarah@gop.org", "john@nowhere.com",
            "joe@nowhere.com", "jack@gop.org", "dan@gop.org"};

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
        assertEquals("there should not be any addresses in the database", 0, allAddresses.size());
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
		Address address = new Address(expectedName, expectedAddress, expectedPhone,expectedEmail);
		addressService.save(address);
		Address result = addressService.getAddressByName(expectedName);

        assertNotNull("surrogate key not returned?", result.getId());
		assertEquals(expectedName, result.getName());
		assertEquals(expectedAddress,result.getAddress());
		assertEquals(expectedPhone,result.getPhone());
		assertEquals(expectedEmail,result.getEmail());

        em.flush();
    }
	
	@Test
    @Rollback
	public void testGetAllAddresses() {
        createTestData();

		List<Address> addresses = addressService.getAllAddresses();
		
		for (int i = 0; i < addresses.size(); i++) {
			Address address = addressService.getAddressByName(testNames[i]);
			assertEquals(testNames[i], address.getName());
			assertEquals(testAddresses[i],address.getAddress());
			assertEquals(testPhoneNumbers[i],address.getPhone());
			assertEquals(testEmailAddresses[i],address.getEmail());
		}
		assertEquals("there should be equal number of addresses as there are expectedName array entries",
                testNames.length, addresses.size());
	}

    void createTestData() {
        for (int i = 0; i < testNames.length; i++) {
            Address address = new Address(testNames[i], testAddresses[i], testPhoneNumbers[i],
                    testEmailAddresses[i]);
            addressService.save(address);
        }

    }
	
	@Test
    @Rollback
	public void testDeleteAddress() {
		String expectedName = "Lloyd Bentsen";
		String expectedAddress = "123 Mission way\nHidalgo, TX";
		String expectedPhone = "+1.999.999.9999";
		String expectedEmail = "lloyd@dnc.org";
		Address address = new Address(expectedName, expectedAddress, expectedPhone,expectedEmail);
		addressService.save(address);
		Address result = addressService.getAddressByName(expectedName);
		addressService.deleteAddress(result);
		result = addressService.getAddressByName(expectedName);
		assertNull("there should be no Lloyd Bentsen after the delete", result);

        em.flush();
	}

    @Test
    @Rollback
    public void testSearchNamePattern() {
        createTestData();

        List<Address> results = addressService.searchAddresses("john", false, null, false, null, false, null, false);
        assertEquals("both of the John's should have matched", 2, results.size());
        boolean foundJohnDoe = false;
        boolean foundJohnEdwards = false;

        for (Address a : results) {
            if (a.getName().equals(testNames[0])) {
                foundJohnDoe = true;
            } else if (a.getName().equals(testNames[2]))  {
                foundJohnEdwards = true;
            }
        }

        assertTrue("did not find John Doe", foundJohnDoe);
        assertTrue("did not find John Edwards", foundJohnEdwards);
    }

    @Test
    @Rollback
    public void testSearchNameExact() {
        createTestData();

        List<Address> results = addressService.searchAddresses("John Doe", true, null, false, null, false, null, false);
        assertEquals("John doe should have matched", 1, results.size());
        boolean foundJohnDoe = false;

        for (Address a : results) {
            if (a.getName().equals(testNames[0])) {
                foundJohnDoe = true;
            }
        }

        assertTrue("did not find John Doe", foundJohnDoe);
    }

    @Test
    @Rollback
    public void testSearchAddressPattern() {
        createTestData();

        List<Address> results = addressService.searchAddresses(null, false, "st", false, null, false, null, false);
        assertEquals("should have found two results", 2, results.size());
        boolean foundJohnDoe = false;
        boolean foundJackKemp = false;

        for (Address a : results) {
            if (a.getName().equals(testNames[0])) {
                foundJohnDoe = true;
            } else if (a.getName().equals(testNames[4]))  {
                foundJackKemp = true;
            }
        }

        assertTrue("did not find John Doe", foundJohnDoe);
        assertTrue("did not find Jack Kemp", foundJackKemp);
    }

    @Test
    @Rollback
    public void testSearchAddressExact() {
        createTestData();

        List<Address> results = addressService.searchAddresses(null, false, testAddresses[1], true, null, false, null, false);
        assertEquals("Sarah Palin should have matched", 1, results.size());
        boolean foundSarahPalin = false;

        for (Address a : results) {
            if (a.getName().equals(testNames[1])) {
                foundSarahPalin = true;
            }
        }

        assertTrue("did not find Sarah Palin", foundSarahPalin);
    }

    @Test
    @Rollback
    public void testSearchEmailPattern() {
        createTestData();

        List<Address> results = addressService.searchAddresses(null, false, null, false, ".com", false, null, false);
        assertEquals("should have found three results", 3, results.size());
        boolean foundJohnDoe = false;
        boolean foundJohnEdwards = false;
        boolean foundJoeLieberman = false;

        for (Address a : results) {
            if (a.getName().equals(testNames[0])) {
                foundJohnDoe = true;
            } else if (a.getName().equals(testNames[2]))  {
                foundJohnEdwards = true;
            } else if (a.getName().equals(testNames[3])) {
                foundJoeLieberman = true;
            }
        }

        assertTrue("did not find John Doe", foundJohnDoe);
        assertTrue("did not find John Edwards", foundJohnEdwards);
        assertTrue("did not find Joe Lieberman", foundJoeLieberman);
    }

    @Test
    @Rollback
    public void testSearchEmailExact() {
        createTestData();

        List<Address> results = addressService.searchAddresses(null, false, null, true, testEmailAddresses[5], true,
                null, false);
        assertEquals("Dan Quayle should have matched", 1, results.size());
        boolean foundDanQuayle = false;

        for (Address a : results) {
            if (a.getName().equals(testNames[5])) {
                foundDanQuayle = true;
            }
        }

        assertTrue("did not find Dan Quayle", foundDanQuayle);
    }

    @Test
    @Rollback
    public void testSearchPhonePattern() {
        createTestData();

        List<Address> results = addressService.searchAddresses(null, false, null, false, null, false, "999", false);
        assertEquals("should have found four results", 4, results.size());

        boolean foundSarahPalin = false;
        boolean foundJohnEdwards = false;
        boolean foundJoeLieberman = false;
        boolean foundJackKemp = false;

        for (Address a : results) {
            if (a.getName().equals(testNames[1])) {
                foundSarahPalin = true;
            } else if (a.getName().equals(testNames[2]))  {
                foundJohnEdwards = true;
            } else if (a.getName().equals(testNames[3])) {
                foundJoeLieberman = true;
            } else if (a.getName().equals(testNames[4])) {
                foundJackKemp = true;
            }
        }

        assertTrue("did not find Sarah Palin", foundSarahPalin);
        assertTrue("did not find John Edwards", foundJohnEdwards);
        assertTrue("did not find Joe Lieberman", foundJoeLieberman);
        assertTrue("did not find Jack Kemp", foundJackKemp);
    }

    @Test
    @Rollback
    public void testSearchPhoneExact() {
        createTestData();

        List<Address> results = addressService.searchAddresses(null, false, null, true, null, false,
                testPhoneNumbers[0], true);
        assertEquals("John Doe should have matched", 1, results.size());
        boolean foundJohnDoe = false;

        for (Address a : results) {
            if (a.getName().equals(testNames[0])) {
                foundJohnDoe = true;
            }
        }

        assertTrue("did not find John Doe", foundJohnDoe);
    }

}
