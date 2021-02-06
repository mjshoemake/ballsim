package baseball.domain

import org.apache.log4j.Logger

abstract class Comparable {
    protected Logger log = Logger.getLogger("Debug");

    boolean compareString(String fieldName, String source, String target) {
        def m = "${C}.compareString() - "
        if (source == null && target == null) {
            log.debug("$m    $fieldName null == null.  OK")
            return true
        } else if (source == null) {
            log.debug("$m    $fieldName null != '$target'.")
            return false
        } else if (target == null) {
            log.debug("$m    $fieldName '$source' != null.")
            return false
        } else if (! source.equals(target)) {
            log.debug("$m    $fieldName '$source' != '$target'.")
            return false
        } else {
            log.debug("$m    $fieldName '$source' == '$target'.  OK")
            return true
        }
    }

    boolean compareString(String fieldName, String source, String target, List builder) {
        def m = "${C}.compareString() - "
        if (source == null && target == null) {
            //builder << "$m    $fieldName null == null.  OK"
            return true
        } else if (source == null) {
            builder << "$m    $fieldName null != '$target'."
            return false
        } else if (target == null) {
            builder << "$m    $fieldName '$source' != null."
            return false
        } else if (! source.equals(target)) {
            builder << "$m    $fieldName '$source' != '$target'."
            return false
        } else {
            //builder << "$m    $fieldName '$source' == '$target'.  OK"
            return true
        }
    }

    boolean compareInt(String fieldName, int source, int target) {
        def m = "${C}.compareInt() - "
        if (source == null && target == null) {
            //log.debug("$m    $fieldName null == null.  OK")
            return true
        } else if (source == null) {
            log.debug("$m    $fieldName null != $target.")
            return false
        } else if (target == null) {
            log.debug("$m    $fieldName $source != null.")
            return false
        } else if (! source == target) {
            log.debug("$m    $fieldName $source != $target.")
            return false
        } else {
            ///log.debug("$m    $fieldName $source == $target.  OK")
            return true
        }
    }

    boolean compareInt(String fieldName, int source, int target, List builder) {
        def m = "${C}.compareInt() - "
        if (source == null && target == null) {
            //builder << "$m    $fieldName null == null.  OK"
            return true
        } else if (source == null) {
            builder << "$m    $fieldName null != $target."
            return false
        } else if (target == null) {
            builder << "$m    $fieldName $source != null."
            return false
        } else if (! source == target) {
            builder << "$m    $fieldName $source != $target."
            return false
        } else {
            //builder << "$m    $fieldName $source == $target.  OK"
            return true
        }
    }

    boolean compareBoolean(String fieldName, boolean source, boolean target) {
        def m = "${C}.compareBoolean() - "
        if (source == null && target == null) {
            //log.debug("$m    $fieldName null == null.  OK")
            return true
        } else if (source == null) {
            log.debug("$m    $fieldName null != $target.")
            return false
        } else if (target == null) {
            log.debug("$m    $fieldName $source != null.")
            return false
        } else if (! source == target) {
            log.debug("$m    $fieldName $source != $target.")
            return false
        } else {
            //log.debug("$m    $fieldName $source == $target.  OK")
            return true
        }
    }

    boolean compareBoolean(String fieldName, boolean source, boolean target, List builder) {
        def m = "${C}.compareBoolean() - "
        if (source == null && target == null) {
            //builder << "$m    $fieldName null == null.  OK"
            return true
        } else if (source == null) {
            builder << "$m    $fieldName null != $target."
            return false
        } else if (target == null) {
            builder << "$m    $fieldName $source != null."
            return false
        } else if (! source == target) {
            builder << "$m    $fieldName $source != $target."
            return false
        } else {
            //builder << "$m    $fieldName $source == $target.  OK"
            return true
        }
    }

    boolean compareDouble(String fieldName, double source, double target) {
        def m = "${C}.compareDouble() - "
        if (source == null && target == null) {
            //log.debug("$m    $fieldName null == null.  OK")
            return true
        } else if (source == null) {
            log.debug("$m    $fieldName null != $target.")
            return false
        } else if (target == null) {
            log.debug("$m    $fieldName $source != null.")
            return false
        } else if (! source == target) {
            log.debug("$m    $fieldName $source != $target.")
            return false
        } else {
            //log.debug("$m    $fieldName $source == $target.  OK")
            return true
        }
    }

    boolean compareDouble(String fieldName, double source, double target, List builder) {
        def m = "${C}.compareDouble() - "
        if (source == null && target == null) {
            //builder << "$m    $fieldName null == null.  OK"
            return true
        } else if (source == null) {
            builder << "$m    $fieldName null != $target."
            return false
        } else if (target == null) {
            builder << "$m    $fieldName $source != null."
            return false
        } else if (! source == target) {
            builder << "$m    $fieldName $source != $target."
            return false
        } else {
            //builder << "$m    $fieldName $source == $target.  OK"
            return true
        }
    }

    boolean compareObject(String fieldName, def source, def target) {
        def m = "${C}.compareObject() - "
        List builder = []
        boolean result = compareObject(fieldName, source, target, builder)
        if (! result) {
            builder.iterator().each() {
                log.debug(it)
            }
            return false
        } else {
            log.debug("$m $fieldName ${source.toString()} == ${target.toString()}. OK")
            return true
        }
    }

    boolean compareObject(String fieldName, def source, def target, List builder) {
        def m = "${C}.compareObject() - "
        try {
            if (source == null && target == null) {
                //builder << "$m $fieldName null == null.  OK"
                return true
            } else if (source == null) {
                builder << "$m $fieldName null != ${target.toString()}."
                return false
            } else if (target == null) {
                builder << "$m $fieldName ${source.toString()} != null."
                return false
            } else if (! source.equals(target, builder)) {
                builder << "$m $fieldName ${source.toString()} != ${target.toString()}."
                return false
            } else {
                //builder << "$m $fieldName ${source.toString()} == ${target.toString()}. OK"
                return true
            }
        } catch (Exception e) {
            builder << "$m $fieldName ERROR: ${e.getMessage()}"
        }
    }

    boolean compareList(String fieldName, def source, def target) {
        def m = "${C}.compareList() - "
        List builder = []
        boolean result = compareList(fieldName, source, target, builder)
        if (! result) {
            builder.iterator().each() {
                log.debug(it)
            }
            return false
        } else {
            log.debug("$m $fieldName [${source.size()}] == [${target.size()}]. OK")
            return true
        }
    }

    boolean compareList(String fieldName, def source, def target, List builder) {
        def m = "${C}.compareList() - "
        try {
            boolean result = true
            if (source == null && target == null) {
                //builder << "$m $fieldName null == null.  OK"
                return true
            } else if (source == null) {
                builder << "$m fieldName null != [${target.size()}]."
                return false
            } else if (target == null) {
                builder << "$m $fieldName [${source.size()}] != null."
                return false
            } else {
                for (int i=0; i <= source.size()-1; i++) {
                    Object sourceItem = source.get(i)
                    Object targetItem = target.get(i)
                    try {
                        if (! sourceItem.equals(targetItem, builder)) {
                            result = false
                        }
                    } catch (Exception e) {
                        if (! sourceItem.equals(targetItem)) {
                            result = false
                        }
                    }
                }

                if (! result) {
                    builder << "$m $fieldName [${source.size()}] != ${target.size()}."
                    return false
                } else {
                    //builder << "$m $fieldName [${source.size()}] == [${target.size()}]. OK"
                    return true
                }
            }
        } catch(Exception e) {
            builder << "$m $fieldName ERROR: ${e.getMessage()}"
            return false
        }
    }

    boolean compareMap(String fieldName, def source, def target) {
        def m = "${C}.compareMap() - "
        List builder = []
        boolean result = compareMap(fieldName, source, target, builder)
        if (! result) {
            builder.iterator().each() {
                log.debug(it)
            }
            return false
        } else {
            log.debug("$m $fieldName [${source.size()}:] == [${target.size()}:]. OK")
            return true
        }
    }

    boolean compareMap(String fieldName, Map source, Map target, List builder) {
        try {
            def m = "${C}.compareMap() - "
            boolean result = true
            if (source == null && target == null) {
                //builder << "$m    $fieldName null == null.  OK"
                return true
            } else if (source == null) {
                builder << "$m    $fieldName null != [${target.size()}:]."
                return false
            } else if (target == null) {
                builder << "$m    $fieldName [${source.size()}:] != null."
                return false
            } else {
                source.keySet().each { key ->
                    Object sourceItem = source[key]
                    Object targetItem = target[key]
                    try {
                        if (! sourceItem.equals(targetItem, builder)) {
                            result = false
                        }
                    } catch (Exception e) {
                        if (! sourceItem.equals(targetItem)) {
                            result = false
                        }
                    }
                }
                if (source.size() != target.size()) {
                    result = false
                }
                if (! result) {
                    builder << "$m    $fieldName [${source.size()}:] != [${target.size()}:]."
                    return false
                } else {
                    //builder << "$m    $fieldName [${source.size()}:] == [${target.size()}:]. OK"
                    return true
                }
            }
        } catch(Exception e) {
            builder << "$m $fieldName ERROR: ${e.getMessage()}"
            return false
        }
    }

}
