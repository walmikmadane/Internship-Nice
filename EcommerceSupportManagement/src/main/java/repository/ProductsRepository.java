package repository;

import domain.entity.Products;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;


@Repository
public interface ProductsRepository extends CrudRepository<Products, String>
{
    List<Products> findByProductId(String id);
    List<Products> findByCategoryName(String categoryname);
}
