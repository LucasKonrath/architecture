package com.poc.cqrs.query.projection;

import com.poc.cqrs.command.event.ProductEvent.ProductCreated;
import com.poc.cqrs.command.event.ProductEvent.ProductDeleted;
import com.poc.cqrs.command.event.ProductEvent.ProductUpdated;
import com.poc.cqrs.query.model.ProductView;
import com.poc.cqrs.query.repository.ProductQueryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductProjection {

    private static final Logger log = LoggerFactory.getLogger(ProductProjection.class);

    private final ProductQueryRepository queryRepository;

    public ProductProjection(ProductQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @EventListener
    @Transactional
    public void on(ProductCreated event) {
        log.info("Projecting ProductCreated: {}", event.productId());
        var view = new ProductView(
                event.productId(), event.name(), event.description(),
                event.price(), event.quantity()
        );
        queryRepository.save(view);
    }

    @EventListener
    @Transactional
    public void on(ProductUpdated event) {
        log.info("Projecting ProductUpdated: {}", event.productId());
        queryRepository.findById(event.productId()).ifPresent(view -> {
            view.update(event.name(), event.description(), event.price(), event.quantity());
            queryRepository.save(view);
        });
    }

    @EventListener
    @Transactional
    public void on(ProductDeleted event) {
        log.info("Projecting ProductDeleted: {}", event.productId());
        queryRepository.deleteById(event.productId());
    }
}
