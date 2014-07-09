package com.pivotal.cf.broker.services;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseService {
	protected <E> List<E> makeCollection(Iterable<E> iter) {
	    List<E> list = new ArrayList<E>();
	    for (E item : iter) {
	        list.add(item);
	    }
	    return list;
	}

}
