package com.osintegrators.example;

import java.util.List;

public interface AddressService { 

	Address save(Address address);

	Address getAddressByName(String expectedName);

	void deleteAddress(Address result);

	List<Address> getAllAddresses();

    List<Address> searchAddresses(String name, boolean nameExact,
            String address, boolean addressExact,
            String email, boolean emailExact,
            String phone, boolean phoneExact);

    List<Address> fullTextSearch(String stringToMatch);
}
