package com.passes;

import java.util.*;
import java.util.stream.Collectors;

public final class PassRepository {

    // Primary store: passId -> Pass
    private final Map<Long, Pass> PassesById = new HashMap<>();

    // Secondary index: holderPersonId -> set of passIds
    private final Map<Long, Set<Long>> PassesByHolder = new HashMap<>();

    public void savePass(Pass pass) {
        PassesById.put(pass.getId(), pass);
        PassesByHolder
                .computeIfAbsent(pass.getOwnerId(), k -> new LinkedHashSet<>())
                .add(pass.getId());
    }

    public Pass getPassById(Long passId) {
        return PassesById.get(passId);
    }

    public List<Pass> getPassesByOwnerId(Long ownerId) {
        Set<Long> ids = PassesByHolder.get(ownerId);
        if (ids == null || ids.isEmpty()) return List.of();
        //unmodifyable is intentional i dont want to try to add a pass here lol
        List<Pass> passes = ids.stream()
                .map(PassesById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
        return passes;
    }

    public void deletePassById(Long passId) {
        Pass removed = PassesById.remove(passId);
        if (removed == null) return;

        Set<Long> set = PassesByHolder.get(removed.getOwnerId());
        if (set != null) {
            set.remove(passId);
            if (set.isEmpty()) {
                PassesByHolder.remove(removed.getOwnerId());
            }
        }
    }

    public boolean passExists(Long passId) {
        return PassesById.containsKey(passId);
    }

    public int count() {
        return PassesById.size();
    }

    public void clearAllPasses() {
        PassesById.clear();
        PassesByHolder.clear();
    }
}
