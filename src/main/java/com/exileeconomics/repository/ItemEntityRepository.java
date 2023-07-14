package com.exileeconomics.repository;

import com.exileeconomics.entity.ItemEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Optional;


@Repository
public interface ItemEntityRepository extends CrudRepository<ItemEntity, Long> {
    // TODO make these HQL queries
    @Query(
            value = "SELECT count(id) FROM item " +
                    "WHERE item_id = :item_id AND sold_quantity < total_quantity AND sold_quantity < :sold_quantity_upper_limit " +
                    "AND created_at BETWEEN :lower_time_limit AND :upper_time_limit",
            nativeQuery = true
    )

    Integer getTotalItemsInBetweenDates(
            @Param("item_id") long itemId,
            @Param("sold_quantity_upper_limit") int soldQuantityUpperLimit,
            @Param("lower_time_limit") Timestamp lowerTimeLimit,
            @Param("upper_time_limit") Timestamp upperTimeLimit
    );  
    
    @Query(
            value = "SELECT FLOOR(AVG(tbl.price)) as price FROM  " +
                    "(SELECT it.price " +
                    "FROM item it LEFT JOIN currency_ratio cr ON it.currency_ratio_id = cr.id " +
                    "WHERE it.item_id = :sold_item_id AND cr.item_definition_entity_id = :sold_for_item_id  " +
                    "  AND it.sold_quantity < it.total_quantity " +
                    "  AND it.total_quantity < :total_quantity " +
                    "  AND it.created_at BETWEEN :lower_time_limit AND :upper_time_limit " +
                    "ORDER BY price LIMIT :limit OFFSET :offset) AS tbl; ",
            nativeQuery = true
    )
    Optional<Integer> getAveragePriceForItem(
            @Param("sold_item_id") long soldItemId,
            @Param("sold_for_item_id") long soldForItemId,
            @Param("total_quantity") int totalQuantity,
            @Param("lower_time_limit") Timestamp lowerTimeLimit,
            @Param("upper_time_limit") Timestamp upperTimeLimit,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(
            value = "SELECT it.* " +
                    "FROM item it LEFT JOIN currency_ratio cr ON it.currency_ratio_id = cr.id " +
                    "WHERE it.item_id = :sold_item_id AND cr.item_definition_entity_id = :sold_for_item_id  " +
                    "AND it.sold_quantity < it.total_quantity " +
                    "AND it.total_quantity < :total_quantity " +
                    "AND it.created_at BETWEEN :lower_time_limit AND :upper_time_limit " +
                    "ORDER BY price LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    Collection<ItemEntity> getPricesForItemsBetweenDatesWithLimitAndOffset(
            @Param("sold_item_id") long soldItemId,
            @Param("sold_for_item_id") long soldForItemId,
            @Param("total_quantity") int totalQuantity,
            @Param("lower_time_limit") Timestamp lowerTimeLimit,
            @Param("upper_time_limit") Timestamp upperTimeLimit,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(
            value = "SELECT it.* " +
                    "FROM item it LEFT JOIN currency_ratio cr ON it.currency_ratio_id = cr.id " +
                    "WHERE it.item_id = :sold_item_id AND cr.item_definition_entity_id = :sold_for_item_id  " +
                    "AND it.sold_quantity < it.total_quantity " +
                    "AND it.total_quantity BETWEEN :quantity_lower_limit AND :quantity_upper_limit " +
                    "AND it.created_at BETWEEN :lower_time_limit AND :upper_time_limit " +
                    "ORDER BY price LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    Collection<ItemEntity> getPricesForItemsBetweenDatesWithLimitAndOffsetAndQuantityBetween(
            @Param("sold_item_id") long soldItemId,
            @Param("sold_for_item_id") long soldForItemId,
            @Param("quantity_lower_limit") int quantity_lower_limit,
            @Param("quantity_upper_limit") int quantity_upper_limit,
            @Param("lower_time_limit") Timestamp lowerTimeLimit,
            @Param("upper_time_limit") Timestamp upperTimeLimit,
            @Param("limit") int limit,
            @Param("offset") int offset
    );
}