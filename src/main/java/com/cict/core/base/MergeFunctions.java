package com.cict.core.base;

import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("SpellCheckingInspection")
public class MergeFunctions {

    /**
     *  Merge Base data no set/list properties
     * @param from
     * @param to
     * @param modelMapper
     * @param <A>
     * @param <B>
     */
    public static <A, B> void mergeBase(A from, B to, ModelMapper modelMapper) {
        setModelMapperBaseMerge(modelMapper);
        modelMapper.getConfiguration().setSkipNullEnabled(true); // added Nov 24, 2022 to not update the null values
        modelMapper.map(from, to);
        modelMapper.getConfiguration().setPropertyCondition( a -> true);
    }

    /**
     * multiple merge base data no set/list properties
     * @param from
     * @param to
     * @param modelMapper
     * @param <A>
     * @param <B>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <A, B> List<B> mergeBase(List<A> from, B to, ModelMapper modelMapper) {
        setModelMapperBaseMerge(modelMapper);
        List<B> ret = new ArrayList<>();
        for (A e : from) {
            Object map = modelMapper.map(e, (Type) to);
            ret.add((B)map);
        }
        modelMapper.getConfiguration().setPropertyCondition( a -> true);
        return ret;
    }

    /**
     * exclud lise/set in the modelmapper maapping -> should be reset righaafter use.
     * @param modelMapper
     */
    private static void setModelMapperBaseMerge(ModelMapper modelMapper) {
        modelMapper.getConfiguration().setPropertyCondition(
                context -> {
                    String type = context.getMapping().getSourceType().getTypeName();
                    return !(type.contains("java/util/SortedSet")
                            || type.contains("java/util/Set")
                            || type.contains("java/util/List")
                            || type.contains("java.util.SortedSet")
                            || type.contains("java.util.Set")
                            || type.contains("java.util.List"));
                }
        );
    }


    // Sample: merge(this.country, entity.getCountry(), entity::setCountry, resolver, modelMapper);
    public static <A extends Mergeable<?>, B> void merge(
            A from,                     // this.country
            B existing,                 // entity.getCountry()
            Consumer<B> to,             // entity::setCountry
            EntityResolver resolver,
            ModelMapper modelMapper
            ) {
        merge(from, existing, to,  null, resolver, modelMapper);
    }


    public static <A, B> void merge(A from, B existing, Consumer<B> to, Supplier<B> factory, EntityResolver resolver, ModelMapper modelMapper) {
        Sameness sameness = sameness(from, existing);
        if (sameness != Sameness.EQUAL) {
            if (from == null) {
                to.accept(null);
            } else {
                if (sameness == Sameness.MERGEABLE) {
                    merge(from, existing, resolver, modelMapper);
                } else if (factory == null && from instanceof Mergeable) {
                    to.accept(resolver.getEntity(from));
                } else if (factory != null) {
                    B dest = factory.get();
                    merge(from, dest, resolver, modelMapper);
                    to.accept(dest);
                }
            }
        }
    }

    public static <A, B> void merge(A from, B to, EntityResolver resolver) {
        merge(from, to, resolver, null);
    }

    @SuppressWarnings("unchecked")
    public static <A, B> void merge(A from, B to, EntityResolver resolver, ModelMapper modelMapper) {
        //PUSH

        // This Mergeable Casting has a warning
        // -> working but may cause some casting issue may need to check in the future
        if (to instanceof Mergeable) {
            ((Mergeable) to).from(from);
        }
        else if (from instanceof Mergeable) {
            if(modelMapper == null)
                ((Mergeable) from).to(to, resolver);
            else
                ((Mergeable) from).to(to, resolver, modelMapper);
        }
        else {
            throw new RuntimeException("At least one side must be Mergeable in merge");
        }

        //POP

    }

    // merge(this.labours, entity.getLabours(), entity::setLabours, StyleExtLabour::new, resolver);
    public static <A, B, C extends Collection<B>> void merge(
            Collection<A> from,         // this.labours,
            Collection<B> existing,     // entity.getLabours()
            Consumer<C> to,             // entity::setLabours
            Supplier<B> factory,         // StyleExtLabour::new
            EntityResolver resolver) {  // resolver
        merge(from, existing, to, factory, resolver, null);
    }

    @SuppressWarnings("unchecked")
    public static <A, B, C extends Collection<B>> void merge(
            Collection<A> from,         // this.labours,
            Collection<B> existing,     // entity.getLabours()
            Consumer<C> to,             // entity::setLabours
            Supplier<B> factory,         // StyleExtLabour::new
            EntityResolver resolver,
            ModelMapper modelMapper) {  // resolver

        if (factory == null) {
            throw new NullPointerException("factory must be non null in merge");
        }

        if (from == null) {
            from = Collections.emptySet();
        }

        Collection<B> dest;
        dest = existing;
        /*
            if (existing == null) {
                if (to == null) {
                    throw new NullPointerException("Cannot have both a null existing collection and a " +
                            "null consumer collection in merge");
                }
                dest = getNewInstance(to);
            } else {
                dest = existing;
            }
        */

        Map<B, List<A>> mergeCandidates = new LinkedHashMap<>();
        Map<A, B> map = new LinkedHashMap<>();
        for (A resource : from) {
            map.put(resource, null);
        }
        // Compare each of the resources to each of the entities. Three things can happen here:
        //
        // 1. If the entity is a compatible match and allows the resource to be merged to it,
        //    the resource and entity get placed into the merge candidates list.
        // 2. If an exact match is found between an entity and a resource, any reference to them
        //    in the merge candidates list is removed and the two objects are linked in the map.
        //    Essentially they become untouchable - like syphilis.
        // 3. If there is no merge or equal match found between the entity and resource, this means
        //    that the resource is new and needs to be added to the entity collection.
        outer:
        for (A resource : from) {
            boolean found = false;
            for (B entity : dest) {
                if (map.containsValue(entity)) {
                    continue;
                }

                Sameness sameness = sameness(resource, entity);
                if (sameness == Sameness.MERGEABLE) {
                    List<A> resourceMergeCandidates = mergeCandidates.computeIfAbsent(entity, k -> new ArrayList<>());
                    if (!resourceMergeCandidates.contains(resource)) {
                        resourceMergeCandidates.add(resource);
                    }
                    found = true;
                } else if (sameness == Sameness.EQUAL) {
                    mergeCandidates.remove(entity);
                    map.put(resource, entity);
                    continue outer;
                }
            }

            if (!found) {
                B entity = factory.get();
                if(modelMapper == null)
                    merge(resource, entity, resolver);
                else
                    merge(resource, entity, resolver, modelMapper);
                map.put(resource, entity);
            }
        }


        // Run through the merge candidates list, applying the best matching entity. The following rules
        // apply here:
        //
        // - If the resource count mapped against the entity is zero, we ignore the entity
        //   this effectively results in the entity being deleted from the destination collection.
        // - If the resource count is one or more than one, the first resource is used for the
        //   merge and the others ignored.
        //
        // This code allows only one resource to be merged to an entity. Once a match is used, the
        // matching resource is removed from all candidate lists to prevent it being reused.
        Set<A> remainder = new HashSet<>();
        List<Map.Entry<B, List<A>>> entries = new ArrayList<>(mergeCandidates.entrySet());
        Comparator<Map.Entry<B, List<A>>> comparator = Comparator.comparingInt(o -> o.getValue().size());
        entries.sort(comparator);

        while (!entries.isEmpty()) {
            Map.Entry<B, List<A>> entry = entries.remove(0);
            B entity = entry.getKey();
            A resource = null;
            List<A> resources = entry.getValue();
            if (resources.size() != 0) {
                resource = resources.remove(0);
                remainder.remove(resource);

                //merge(resource, entity, resolver, modelMapper);
                if(modelMapper == null)
                    merge(resource, entity, resolver);
                else
                    merge(resource, entity, resolver, modelMapper);
                map.put(resource, entity);

                remainder.addAll(resources);
            }

            A a = resource;
            entries.forEach(e -> e.getValue().remove(a));
            entries.sort(comparator);
        }

        // Add any remaining resources.
        remainder.forEach(resource -> {
//            B entity = factory != null ? factory.get() : resolver.getEntity((Mergeable<B>) resource);
            B entity = factory.get();

            //merge(resource, entity, resolver, modelMapper);
            if(modelMapper == null)
                merge(resource, entity, resolver);
            else
                merge(resource, entity, resolver, modelMapper);

            map.put(resource, entity);
        });

        // Remove any entities that are not in the map and add any new entities.
        dest.retainAll(map.values().stream().filter(Objects::nonNull).collect(Collectors.toList()));
        dest.addAll(map.values().stream().filter(entity -> !dest.contains(entity)).collect(Collectors.toList()));

        // Set the destination into the consumer regardless because some getters return
        // temporary collections. By assigning the value to the consumer, we ensure the entity
        // processes the collection even if the collection began life detached.
        if (to != null) {
            to.accept((C) dest);
        }

    } //End of merge

    /* ------------------------------------------------------------------------------------------------------- */
    /* INTERNAL FUNCTIONS                                                                                      */
    /* ------------------------------------------------------------------------------------------------------- */

    /**
     * Returns the {@link Sameness} between the two objects.
     */
    @SuppressWarnings("unchecked")
    private static <A, B> Sameness sameness(A a, B b) {
        if (b == a) {
            return Sameness.EQUAL;
        }

        if (b == null || a == null) {
            return Sameness.DIFFERENT;
        }

        // Don't change the order of this check. When A and B are both mergeable,
        // the sameness check is performed on B.
        // NOTES: I hust change this from comment below? hehehe 2021 - dec
        if (b instanceof Mergeable) {
            return ((Mergeable) b).sameness(a);
        } else if (a instanceof Mergeable) {
            return ((Mergeable) a).sameness(b);
        }
        /*
            if (b instanceof BiMergeable && --a instanceof Mergeable) {
                return ((BiMergeable) b).sameness((Mergeable) a);
            } else if (b instanceof Mergeable) {
                return ((Mergeable) b).sameness(a);
            } else if (a instanceof Mergeable) {
                return ((Mergeable) a).sameness(b);
            }
        */

        throw new RuntimeException("At least one side must be mergeable in merge");
    }
}
