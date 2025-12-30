package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.SaleItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    @Query("SELECT si FROM SaleItem si WHERE si.sale.id = :saleId")
    List<SaleItem> findBySale(@Param("saleId") Long saleId);

    @Query("SELECT si.jar.name, SUM(si.quantity) AS jarSaleCount FROM SaleItem si WHERE si.itemType = 'JAR' GROUP BY si.jar.name")
    List<Object[]> findAllJarItems();

    @Query("SELECT si.jar.name, SUM(si.quantity) AS jarSaleCount FROM SaleItem si WHERE si.itemType = 'JAR' AND si.jar.name LIKE %:name% GROUP BY si.jar.name")
    List<Object[]> findAllJarItems(@Param("name") String name);

    @Query("SELECT DISTINCT si.sale.client.name FROM SaleItem si WHERE si.itemType = 'JAR' AND si.jar.name LIKE %:name%")
    List<Object[]> findAllClientsByJarName(@Param("name") String name);

    @Query("SELECT si.extracto.name, SUM(si.quantity) AS extractSaleCount FROM SaleItem si WHERE si.itemType = 'EXTRACTO' GROUP BY si.extracto.name")
    List<Object[]> findAllExtractItems();

    @Query("SELECT si.extracto.name, SUM(si.quantity) AS extractSaleCount FROM SaleItem si WHERE si.itemType = 'EXTRACTO' AND si.extracto.name LIKE %:name% GROUP BY si.extracto.name")
    List<Object[]> findAllExtractItems(@Param("name") String name);

    @Query("SELECT DISTINCT si.sale.client.name FROM SaleItem si WHERE si.itemType = 'EXTRACTO' AND si.extracto.name LIKE %:name%")
    List<Object[]> findAllClientsByExtractName(@Param("name") String name);

    @Query("SELECT si.capColor.cap.name, si.capColor.color, SUM(si.quantity) AS capColorSaleCount FROM SaleItem si WHERE si.itemType = 'CAP' GROUP BY si.capColor.cap.name")
    List<Object[]> findAllCapItems();

    @Query("SELECT si.capColor.cap.name, si.capColor.color, SUM(si.quantity) AS capColorSaleCount FROM SaleItem si WHERE si.itemType = 'CAP' AND si.capColor.cap.name LIKE %:name% GROUP BY si.capColor.cap.name")
    List<Object[]> findAllCapItems(@Param("name") String name);

    @Query("SELECT DISTINCT si.sale.client.name FROM SaleItem si WHERE si.itemType = 'CAP' AND si.capColor.cap.name LIKE %:name% AND si.capColor.color LIKE %:color%")
    List<Object[]> findAllClientsByCapName(@Param("name") String name, @Param("color") String color);

    @Query("SELECT si.quimico.name, SUM(si.quantity) AS quimicoSaleCount FROM SaleItem si WHERE si.itemType = 'QUIMICO' GROUP BY si.quimico.name")
    List<Object[]> findAllQuimicoItems();

    @Query("SELECT si.quimico.name, SUM(si.quantity) AS quimicoSaleCount FROM SaleItem si WHERE si.itemType = 'QUIMICO' AND si.quimico.name LIKE %:name% GROUP BY si.quimico.name")
    List<Object[]> findAllQuimicoItems(@Param("name") String name);

    @Query("SELECT DISTINCT si.sale.client.name FROM SaleItem si WHERE si.itemType = 'QUIMICO' AND si.quimico.name LIKE %:name%")
    List<Object[]> findAllClientsByQuimicoName(@Param("name") String name);

    @Query("SELECT si.combo.name, SUM(si.quantity) AS comboSaleCount FROM SaleItem si WHERE si.itemType = 'COMBO' GROUP BY si.combo.name")
    List<Object[]> findAllComboItems();

    @Query("SELECT si.combo.name, SUM(si.quantity) AS comboSaleCount FROM SaleItem si WHERE si.itemType = 'COMBO' AND si.combo.name LIKE %:name% GROUP BY si.combo.name")
    List<Object[]> findAllComboItems(@Param("name") String name);

    @Query("SELECT DISTINCT si.sale.client.name FROM SaleItem si WHERE si.itemType = 'COMBO' AND si.combo.name LIKE %:name%")
    List<Object[]> findAllClientsByComboName(@Param("name") String name);

}
    