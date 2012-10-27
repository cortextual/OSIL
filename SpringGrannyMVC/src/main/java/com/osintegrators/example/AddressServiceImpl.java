package com.osintegrators.example;

import java.util.List;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

	@Autowired
	AddressRepository repository;

    @PersistenceContext
    EntityManager em;
	
	public Address save(Address address) {
		return repository.save(address);

	}

	public Address getAddressByName(String expectedName) {
		Address address = repository.findByName(expectedName);
		return address;
	}

	public void deleteAddress(Address address) {
		repository.delete(address);
	}

	public List<Address> getAllAddresses() {
		return repository.findAll();
	}

    /**
     * This is implemented manually to show just how bad this query is. It would
     * also be a bit complex to expect Spring Data JPA to implement
     * automatically based on method name due to the optional exact match for
     * each parameter.
     *
     * @param name query for the name field
     * @param nameExact whether to do an exact match on the name field query string
     * @param address query for the address field
     * @param addressExact whether to do an exact match on the address field query string
     * @param email query for the email field
     * @param emailExact whether to do an exact match on the email field query string
     * @param phone query for the phone field
     * @param phoneExact whether to do an exact match on the phone field query string                  
     *             
     * @return list of Address objects that match the search pattern
     */
    @Override
    public List<Address> searchAddresses(String name, boolean nameExact,
            String address, boolean addressExact,
            String email, boolean emailExact,
            String phone, boolean phoneExact) {

        boolean addedOneCondition = false;
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT a FROM Address a");

        if (name != null || address != null || email != null || phone != null ) {
            sb.append(" WHERE ");
        }

        if (name != null) {
            addedOneCondition = true;
            if (nameExact) {
                sb.append(" a.name = :name");
            } else {
                sb.append(" LOWER(a.name) LIKE :name");
            }
        }

        if (address != null) {
            if (addedOneCondition) {
                sb.append(" OR ");
            } else {
                addedOneCondition = true;
            }

            if (addressExact) {
                sb.append(" a.address = :address ");
            } else {
                sb.append(" LOWER(a.address) LIKE :address");
            }
        }

        if (email != null) {
            if (addedOneCondition) {
                sb.append(" OR ");
            } else {
                addedOneCondition = true;
            }

            if (emailExact) {
                sb.append(" a.email = :email");
            } else {
                sb.append(" LOWER(a.email) LIKE :email");
            }
        }

        if (phone != null) {
            if (addedOneCondition) {
                sb.append(" OR ");
            } else {
                addedOneCondition = true;
            }

            if (phoneExact) {
                sb.append(" a.phone = :phone");
            } else {
                sb.append(" LOWER(a.phone) LIKE :phone");
            }
        }

        Query q = em.createQuery(sb.toString(), Address.class);
        if (name != null) {
            if (nameExact) {
                q.setParameter("name", name);
            } else {
                q.setParameter("name", "%" + name + "%");
            }
        }
        if (address != null) {
            if (addressExact) {
                q.setParameter("address", address);
            } else {
                q.setParameter("address", "%" + address + "%");
            }
        }
        if (email != null) {
            if (emailExact) {
                q.setParameter("email", email);
            } else {
                q.setParameter("email", "%" + email + "%");
            }
        }
        if (phone != null) {
            if (phoneExact) {
                q.setParameter("phone", phone);
            } else {
                q.setParameter("phone", "%" + phone + "%");
            }
        }

        return q.getResultList();
    }

    /**
     * Do a full-text search on the Address entity.
     *
     * This is based on the Example 1.9 of the Hibernate Search 3.4 reference documentation.
     *
     * @param stringToMatch what to search for
     * @return a list of search results
     */
    @Override
    public List<Address> fullTextSearch(String stringToMatch) {
        FullTextEntityManager ftem = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);

        // build up a Lucene query
        QueryBuilder qb = ftem.getSearchFactory().buildQueryBuilder().forEntity(Address.class).get();
        org.apache.lucene.search.Query luceneQuery = qb
                .keyword()
                .onFields("name", "address", "phone", "email")
                .matching(stringToMatch.toLowerCase())
                .createQuery();

        // wrap the Lucene query in a JPA query
        Query jpaWrappedQuery = ftem.createFullTextQuery(luceneQuery, Address.class);

        return jpaWrappedQuery.getResultList();
    }

}
