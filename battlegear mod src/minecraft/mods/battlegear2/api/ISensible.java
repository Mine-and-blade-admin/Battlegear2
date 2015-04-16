package mods.battlegear2.api;

import com.google.common.base.Predicate;

import java.util.Iterator;

public interface ISensible<T> {
    /**
     * @return true if first arg is different from second arg
     */
    public boolean differenciate(T holder1, T holder2);

    /**
     * Predicate filtering, uses a comparing object of same type and iterating over multiple instance of above interface
     * @param <T>
     */
    final class Filter<T> implements Predicate<T> {
        private final Iterator<ISensible<T>> senses;
        private final T toCompare;
        public Filter(T compare, Iterator<ISensible<T>> sensitivities){
            this.toCompare = compare;
            this.senses = sensitivities;
        }

        public Filter(T compare, Iterable<ISensible<T>> sensibles){
            this(compare, sensibles.iterator());
        }

        @Override
        public boolean apply(T input) {
            while(senses.hasNext()){
                if(senses.next().differenciate(input, toCompare)){
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean equals(Object object){
            if(this == object){
                return true;
            }
            return object!=null && object instanceof Filter<?> && this.toCompare.equals(((Filter<?>) object).toCompare) && this.senses.equals(((Filter<?>) object).senses);
        }
    }
}
