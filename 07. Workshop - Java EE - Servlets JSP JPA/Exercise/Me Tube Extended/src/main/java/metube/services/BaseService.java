package metube.services;

import metube.domain.entities.Identifiable;
import metube.domain.models.binding.Bindable;
import metube.domain.models.view.Viewable;
import metube.repositories.CrudRepository;
import org.modelmapper.ModelMapper;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

abstract class BaseService<E extends Identifiable<I>, I, R extends CrudRepository<E, I>> implements Service<E, I> {

    protected final Validator validator;
    protected final ModelMapper mapper;
    protected final R repository;
    private final Class<E> entityClass;

    protected BaseService(ModelMapper mapper, Validator validator, R repository) {
        entityClass = initEntityClass();
        this.mapper = mapper;
        this.validator = validator;
        this.repository = repository;
    }

    protected abstract Logger logger();

    protected final <M extends Bindable<E>> boolean create(M model) {
        return validateModel(model) && repository.create(mapper.map(model, entityClass)).isPresent();
    }

    @Override
    public final <M extends Viewable<E>> Optional<M> findById(I id, Class<M> clazz) {
        return repository
                .read(id)
                .map(e -> mapper.map(e, clazz));
    }

    @Override
    public final <M extends Viewable<E>> List<M> findAll(Class<M> clazz) {
        return repository
                .all()
                .stream()
                .map(t -> mapper.map(t, clazz))
                .collect(Collectors.toList());
    }

    private <M extends Bindable<E>> boolean validateModel(M model) {
        Set<ConstraintViolation<M>> violations = validator.validate(model);
        if (!violations.isEmpty()) {
            String msg = "Failed validation on:\r\n\t" +
                    violations.stream()
                            .map(cv -> cv.getPropertyPath().toString()
                                    + " (" + cv.getInvalidValue() + ") " + cv.getMessage())
                            .collect(Collectors.joining("\r\n\t"));
            logger().log(Level.SEVERE, msg);
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private Class<E> initEntityClass() {
        return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
