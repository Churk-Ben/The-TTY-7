package com.tty7.core.save;

public interface SaveStateRepository {
    SaveState load();

    void save(SaveState saveState);
}
