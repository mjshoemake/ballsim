package baseball.domain


class BidirectionalHashMap extends HashMap {

    private HashMap reverseMap = new HashMap()

    void clear() {
        super.clear()
        reverseMap.clear()
    }

    Object getKeyForValue(Object value) {
        return reverseMap.get(value)
    }

    Object put(Object key, Object value) {
        Object origValue = super.get(key)
        if (origValue != null) {
            super.remove(key)
            reverseMap.remove(origValue)
        }
        super.put(key, value)
        return reverseMap.put(value, key)
    }

    Object remove(Object key) {
        Object value = super.get(key)
        super.remove(key)
        reverseMap.remove(value)
    }

    Object replace(Object key, Object value) {
        Object origValue = super.get(key)
        if (origValue != null) {
            super.remove(key)
            reverseMap.remove(origValue)
        }
        super.put(key, value)
        reverseMap.put(value, key)
    }

}
