/**
 * 
 */
package com.zhixiangli.smartgomoku.common;

/**
 * pair
 * 
 * @author lizhixiang
 * @date 2015年6月6日
 */
public class Pair<A, B> {

    /**
     * first value.
     */
    private A first;

    /**
     * second value.
     */
    private B second;

    /**
     * getter method for property first
     * 
     * @return the first
     */
    public A getFirst() {
	return first;
    }

    /**
     * setter method for property first
     * 
     * @param first
     *            the first to set
     */
    public void setFirst(A first) {
	this.first = first;
    }

    /**
     * getter method for property second
     * 
     * @return the second
     */
    public B getSecond() {
	return second;
    }

    /**
     * setter method for property second
     * 
     * @param second
     *            the second to set
     */
    public void setSecond(B second) {
	this.second = second;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Pair [first=" + first + ", second=" + second + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((first == null) ? 0 : first.hashCode());
	result = prime * result + ((second == null) ? 0 : second.hashCode());
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof Pair)) {
	    return false;
	}
	Pair<?, ?> other = (Pair<?, ?>) obj;
	if (first == null) {
	    if (other.first != null) {
		return false;
	    }
	} else if (!first.equals(other.first)) {
	    return false;
	}
	if (second == null) {
	    if (other.second != null) {
		return false;
	    }
	} else if (!second.equals(other.second)) {
	    return false;
	}
	return true;
    }

    /**
     * @param first
     * @param second
     */
    public Pair(A first, B second) {
	super();
	this.first = first;
	this.second = second;
    }

}
