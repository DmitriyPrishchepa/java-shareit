package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    <S extends User> S save(S entity);

    @Override
    User getReferenceById(Long aLong);

    @Override
    void deleteById(Long aLong);

    User findUserByEmailContaining(String email);

    boolean existsByEmail(String email);

    boolean existsById(Long id);
}
