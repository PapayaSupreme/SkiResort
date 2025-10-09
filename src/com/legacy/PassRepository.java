package com.legacy;

import com.passes.Pass;

import java.util.*;
import java.util.stream.Collectors;

public final class PassRepository implements CrudRepository<Pass> {

    // Primary store: passId -> Pass
    private final Map<Long, Pass> passesById = new HashMap<>();

    // Secondary index: holderPersonId -> set of passIds
    private final Map<Long, Set<Long>> passesByOwnerId = new HashMap<>();

    @Override
    public void save(Pass pass) {
        passesById.put(pass.getId(), pass);
        passesByOwnerId.computeIfAbsent(pass.getOwnerId(),
                        k -> new LinkedHashSet<>()).add(pass.getId());
    }

    @Override
    public Pass getById(Long id) {
        return passesById.get(id);
    }

    public List<Pass> getPassesByOwnerId(Long ownerId) {
        Set<Long> ids = passesByOwnerId.get(ownerId);
        if (ids == null || ids.isEmpty()) return List.of();
        //unmodifiable is intentional i don't want to try to add a pass here lol
        List<Pass> passes = ids.stream()
                .map(passesById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
        return passes;
    }

    @Override
    public void deleteById(Long id) {
        Pass removed = passesById.remove(id);
        if (removed == null) return;

        Set<Long> set = passesByOwnerId.get(removed.getOwnerId());
        if (set != null) {
            set.remove(id);
            if (set.isEmpty()) {
                passesByOwnerId.remove(removed.getOwnerId());
            }
        }
    }

    @Override
    public boolean exists(Long passId) {
        return passesById.containsKey(passId);
    }

    @Override
    public int count() {
        return passesById.size();
    }

    @Override
    public void clearAll() {
        passesById.clear();
        passesByOwnerId.clear();
    }
}
