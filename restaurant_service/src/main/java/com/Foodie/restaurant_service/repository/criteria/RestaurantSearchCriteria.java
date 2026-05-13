package com.Foodie.restaurant_service.repository.criteria;

import com.Foodie.restaurant_service.entity.Restaurant;
import com.Foodie.restaurant_service.request.restaurants.SearchRestaurantRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class RestaurantSearchCriteria implements Specification<Restaurant> {

    private final SearchRestaurantRequest request;

    @Override
    public Predicate toPredicate(
            @NotNull Root<Restaurant> root,
            CriteriaQuery<?> query,
            @NotNull CriteriaBuilder criteriaBuilder
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if(Objects.nonNull(request.getRestaurantName())){
            predicates.add(criteriaBuilder.like(root.get(Restaurant.RESTAURANT_NAME), "%" + request.getRestaurantName() + "%"));
        }

        if (Objects.nonNull(request.getCity())){
            predicates.add(criteriaBuilder.like(root.get(Restaurant.CITY), "%" + request.getCity() + "%"));
        }

        if (Objects.nonNull(request.getAddress())){
            predicates.add(criteriaBuilder.like(root.get(Restaurant.ADDRESS), "%" + request.getAddress() + "%"));
        }

        if (Objects.nonNull(request.getType())) {
            predicates.add(criteriaBuilder.equal(root.get(Restaurant.TYPE), request.getType()));
        }

        if(Objects.nonNull(request.getKeyword())){
            // OR (хотя бы одно условие)
            Predicate keywordPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(root.get(Restaurant.ADDRESS), "%" + request.getKeyword() + "%"),
                    criteriaBuilder.like(root.get(Restaurant.RESTAURANT_NAME), "%" + request.getKeyword() + "%"),
                    criteriaBuilder.like(root.get(Restaurant.CITY), "%" + request.getKeyword() + "%")
            );
            predicates.add(keywordPredicate);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
