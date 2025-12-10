package com.chang1o.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("PantryItem模型测试")
class PantryItemTest {

    private PantryItem pantryItem;
    private User user;
    private Ingredient ingredient;
    private LocalDate today;
    private LocalDate futureDate;
    private LocalDate pastDate;

    @BeforeEach
    void setUp() {
        pantryItem = new PantryItem();
        user = new User(1, "testuser", "password");
        ingredient = new Ingredient(1, "Tomato");
        today = LocalDate.now();
        futureDate = today.plusDays(30);
        pastDate = today.minusDays(10);
    }

    @Test
    @DisplayName("测试默认构造函数")
    void testDefaultConstructor() {
        PantryItem defaultItem = new PantryItem();
        assertThat(defaultItem).isNotNull();
        assertThat(defaultItem.getId()).isEqualTo(0);
        assertThat(defaultItem.getUserId()).isEqualTo(0);
        assertThat(defaultItem.getIngredientId()).isEqualTo(0);
        assertThat(defaultItem.getQuantity()).isNull();
        assertThat(defaultItem.getExpiryDate()).isNull();
        assertThat(defaultItem.getUser()).isNull();
        assertThat(defaultItem.getIngredient()).isNull();
    }

    @Test
    @DisplayName("测试带参数的构造函数")
    void testConstructorWithParameters() {
        int userId = 123;
        int ingredientId = 456;
        String quantity = "2kg";
        
        PantryItem itemWithParams = new PantryItem(userId, ingredientId, quantity, futureDate);
        
        assertThat(itemWithParams).isNotNull();
        assertThat(itemWithParams.getUserId()).isEqualTo(userId);
        assertThat(itemWithParams.getIngredientId()).isEqualTo(ingredientId);
        assertThat(itemWithParams.getQuantity()).isEqualTo(quantity);
        assertThat(itemWithParams.getExpiryDate()).isEqualTo(futureDate);
        assertThat(itemWithParams.getId()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试完整构造函数")
    void testConstructorWithAllParameters() {
        int id = 1;
        int userId = 123;
        int ingredientId = 456;
        String quantity = "500g";
        
        PantryItem itemWithAll = new PantryItem(id, userId, ingredientId, quantity, futureDate);
        
        assertThat(itemWithAll).isNotNull();
        assertThat(itemWithAll.getId()).isEqualTo(id);
        assertThat(itemWithAll.getUserId()).isEqualTo(userId);
        assertThat(itemWithAll.getIngredientId()).isEqualTo(ingredientId);
        assertThat(itemWithAll.getQuantity()).isEqualTo(quantity);
        assertThat(itemWithAll.getExpiryDate()).isEqualTo(futureDate);
    }

    @Test
    @DisplayName("测试set和get方法")
    void testSetAndGetMethods() {
        int expectedId = 5;
        int expectedUserId = 100;
        int expectedIngredientId = 200;
        String expectedQuantity = "1kg";
        LocalDate expectedExpiryDate = futureDate;
        
        pantryItem.setId(expectedId);
        pantryItem.setUserId(expectedUserId);
        pantryItem.setIngredientId(expectedIngredientId);
        pantryItem.setQuantity(expectedQuantity);
        pantryItem.setExpiryDate(expectedExpiryDate);
        pantryItem.setUser(user);
        pantryItem.setIngredient(ingredient);
        
        assertThat(pantryItem.getId()).isEqualTo(expectedId);
        assertThat(pantryItem.getUserId()).isEqualTo(expectedUserId);
        assertThat(pantryItem.getIngredientId()).isEqualTo(expectedIngredientId);
        assertThat(pantryItem.getQuantity()).isEqualTo(expectedQuantity);
        assertThat(pantryItem.getExpiryDate()).isEqualTo(expectedExpiryDate);
        assertThat(pantryItem.getUser()).isEqualTo(user);
        assertThat(pantryItem.getIngredient()).isEqualTo(ingredient);
    }

    @Test
    @DisplayName("测试isExpired方法")
    void testIsExpiredMethod() {
        pantryItem.setExpiryDate(futureDate);
        assertThat(pantryItem.isExpired()).isFalse();
        
        pantryItem.setExpiryDate(today);
        assertThat(pantryItem.isExpired()).isFalse();
        
        pantryItem.setExpiryDate(pastDate);
        assertThat(pantryItem.isExpired()).isTrue();
        
        pantryItem.setExpiryDate(null);
        assertThat(pantryItem.isExpired()).isFalse();
    }

    @Test
    @DisplayName("测试getDaysUntilExpiry方法")
    void testGetDaysUntilExpiryMethod() {
        pantryItem.setExpiryDate(futureDate);
        int daysUntilExpiry = pantryItem.getDaysUntilExpiry();
        assertThat(daysUntilExpiry).isEqualTo(30);
        assertThat(daysUntilExpiry).isPositive();
        
        pantryItem.setExpiryDate(today);
        assertThat(pantryItem.getDaysUntilExpiry()).isEqualTo(0);
        
        pantryItem.setExpiryDate(pastDate);
        int daysPast = pantryItem.getDaysUntilExpiry();
        assertThat(daysPast).isEqualTo(-10);
        assertThat(daysPast).isNegative();
        
        pantryItem.setExpiryDate(null);
        assertThat(pantryItem.getDaysUntilExpiry()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("测试getDaysUntilExpiry边界情况")
    void testGetDaysUntilExpiryBoundaryCases() {
        LocalDate tomorrow = today.plusDays(1);
        pantryItem.setExpiryDate(tomorrow);
        assertThat(pantryItem.getDaysUntilExpiry()).isEqualTo(1);
        
        LocalDate yesterday = today.minusDays(1);
        pantryItem.setExpiryDate(yesterday);
        assertThat(pantryItem.getDaysUntilExpiry()).isEqualTo(-1);
        
        LocalDate nextYear = today.plusYears(1);
        pantryItem.setExpiryDate(nextYear);
        assertThat(pantryItem.getDaysUntilExpiry()).isGreaterThanOrEqualTo(365);
        
        LocalDate lastYear = today.minusYears(1);
        pantryItem.setExpiryDate(lastYear);
        assertThat(pantryItem.getDaysUntilExpiry()).isLessThanOrEqualTo(-365);
    }

    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        int id = 1;
        int userId = 100;
        int ingredientId = 200;
        String quantity = "2kg";
        LocalDate expiryDate = futureDate;
        
        pantryItem.setId(id);
        pantryItem.setUserId(userId);
        pantryItem.setIngredientId(ingredientId);
        pantryItem.setQuantity(quantity);
        pantryItem.setExpiryDate(expiryDate);
        
        String result = pantryItem.toString();
        
        assertThat(result).contains("PantryItem{");
        assertThat(result).contains("id=" + id);
        assertThat(result).contains("userId=" + userId);
        assertThat(result).contains("ingredientId=" + ingredientId);
        assertThat(result).contains("quantity='" + quantity + "'");
        assertThat(result).contains("expiryDate=" + expiryDate);
    }

    @Test
    @DisplayName("测试toString方法包含null值")
    void testToStringWithNullValues() {
        String result = pantryItem.toString();
        
        assertThat(result).contains("PantryItem{");
        assertThat(result).contains("id=0");
        assertThat(result).contains("userId=0");
        assertThat(result).contains("ingredientId=0");
        assertThat(result).contains("quantity='null'");
        assertThat(result).contains("expiryDate=null");
    }

    @Test
    @DisplayName("测试数量字段的各种格式")
    void testQuantityFieldFormats() {
        String[] quantities = {
            "1kg", "500g", "2L", "3个", "半杯", "适量",
            "1/2杯", "2-3个", ">100g", "<500ml"
        };
        
        for (String quantity : quantities) {
            pantryItem.setQuantity(quantity);
            assertThat(pantryItem.getQuantity()).isEqualTo(quantity);
            
            String toStringResult = pantryItem.toString();
            assertThat(toStringResult).contains("quantity='" + quantity + "'");
        }
    }

    @Test
    @DisplayName("测试LocalDate处理")
    void testLocalDateHandling() {
        LocalDate[] testDates = {
            LocalDate.of(2024, 12, 31),
            LocalDate.parse("2024-12-31"),
            LocalDate.now(),
            LocalDate.MIN,
            LocalDate.MAX
        };
        
        for (LocalDate testDate : testDates) {
            pantryItem.setExpiryDate(testDate);
            assertThat(pantryItem.getExpiryDate()).isEqualTo(testDate);
            
            if (testDate == LocalDate.MIN) {
                assertThat(pantryItem.isExpired()).isTrue();
            } else if (testDate == LocalDate.MAX) {
                assertThat(pantryItem.isExpired()).isFalse();
            }
        }
    }

    @Test
    @DisplayName("测试关联对象")
    void testAssociatedObjects() {
        pantryItem.setUser(user);
        pantryItem.setIngredient(ingredient);
        
        assertThat(pantryItem.getUser()).isEqualTo(user);
        assertThat(pantryItem.getIngredient()).isEqualTo(ingredient);
        
        // 测试设置为null
        pantryItem.setUser(null);
        pantryItem.setIngredient(null);
        
        assertThat(pantryItem.getUser()).isNull();
        assertThat(pantryItem.getIngredient()).isNull();
    }

    @Test
    @DisplayName("测试边界值")
    void testBoundaryValues() {
        pantryItem.setId(Integer.MIN_VALUE);
        assertThat(pantryItem.getId()).isEqualTo(Integer.MIN_VALUE);
        
        pantryItem.setId(Integer.MAX_VALUE);
        assertThat(pantryItem.getId()).isEqualTo(Integer.MAX_VALUE);
        
        pantryItem.setUserId(Integer.MIN_VALUE);
        pantryItem.setIngredientId(Integer.MAX_VALUE);
        
        assertThat(pantryItem.getUserId()).isEqualTo(Integer.MIN_VALUE);
        assertThat(pantryItem.getIngredientId()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("测试字符串字段边界值")
    void testStringFieldBoundaryValues() {
        pantryItem.setQuantity("");
        assertThat(pantryItem.getQuantity()).isEmpty();
        
        String longQuantity = "a".repeat(1000);
        pantryItem.setQuantity(longQuantity);
        assertThat(pantryItem.getQuantity()).isEqualTo(longQuantity);
        
        pantryItem.setQuantity(null);
        assertThat(pantryItem.getQuantity()).isNull();
    }

    @Test
    @DisplayName("测试完整的PantryItem状态")
    void testCompletePantryItemState() {
        PantryItem completeItem = new PantryItem(
            1, // id
            100, // userId
            200, // ingredientId
            "1kg", // quantity
            futureDate // expiryDate
        );
        
        completeItem.setUser(user);
        completeItem.setIngredient(ingredient);
        
        assertThat(completeItem.getId()).isEqualTo(1);
        assertThat(completeItem.getUserId()).isEqualTo(100);
        assertThat(completeItem.getIngredientId()).isEqualTo(200);
        assertThat(completeItem.getQuantity()).isEqualTo("1kg");
        assertThat(completeItem.getExpiryDate()).isEqualTo(futureDate);
        assertThat(completeItem.getUser()).isEqualTo(user);
        assertThat(completeItem.getIngredient()).isEqualTo(ingredient);
        assertThat(completeItem.isExpired()).isFalse();
        assertThat(completeItem.getDaysUntilExpiry()).isEqualTo(30);
    }


    @Test
    @DisplayName("测试构造函数null参数处理")
    void testConstructorNullHandling() {
        PantryItem item = new PantryItem(1, 100, 200, null, futureDate);
        assertThat(item.getQuantity()).isNull();
        assertThat(item.getExpiryDate()).isEqualTo(futureDate);

        PantryItem item2 = new PantryItem(1, 100, 200, "1kg", null);
        assertThat(item2.getQuantity()).isEqualTo("1kg");
        assertThat(item2.getExpiryDate()).isNull();
        assertThat(item2.isExpired()).isFalse();
        assertThat(item2.getDaysUntilExpiry()).isEqualTo(Integer.MAX_VALUE);

        PantryItem item3 = new PantryItem(1, 100, 200, null, null);
        assertThat(item3.getQuantity()).isNull();
        assertThat(item3.getExpiryDate()).isNull();
    }

    @Test
    @DisplayName("测试ID的极值处理")
    void testIdBoundaryValues() {
        pantryItem.setId(Integer.MIN_VALUE);
        assertThat(pantryItem.getId()).isEqualTo(Integer.MIN_VALUE);

        pantryItem.setId(Integer.MAX_VALUE);
        assertThat(pantryItem.getId()).isEqualTo(Integer.MAX_VALUE);

        pantryItem.setId(0);
        assertThat(pantryItem.getId()).isZero();
    }

    @Test
    @DisplayName("测试闰年的日期计算")
    void testLeapYearDateCalculation() {
        LocalDate leapDate = LocalDate.of(2028, 2, 29);
        pantryItem.setExpiryDate(leapDate);

        assertThat(pantryItem.getExpiryDate().getYear()).isEqualTo(2028);
        assertThat(pantryItem.getExpiryDate().getMonthValue()).isEqualTo(2);
        assertThat(pantryItem.getExpiryDate().getDayOfMonth()).isEqualTo(29);

        int daysUntilExpiry = pantryItem.getDaysUntilExpiry();
        assertThat(daysUntilExpiry).isGreaterThan(0);
    }

    @Test
    @DisplayName("测试equals方法")
    void testEqualsMethod() {
        PantryItem item1 = new PantryItem(1, 100, 200, "1kg", futureDate);
        PantryItem item2 = new PantryItem(1, 100, 200, "1kg", futureDate);
        PantryItem item3 = new PantryItem(2, 100, 200, "1kg", futureDate);
        PantryItem item4 = new PantryItem(1, 101, 200, "1kg", futureDate);
        PantryItem item5 = new PantryItem(1, 100, 201, "1kg", futureDate);
        PantryItem item6 = new PantryItem(1, 100, 200, "2kg", futureDate);
        PantryItem item7 = new PantryItem(1, 100, 200, "1kg", pastDate);

        assertThat(item1).isEqualTo(item1);
        assertThat(item1).isEqualTo(item2);
        assertThat(item1).isNotEqualTo(item3);
        assertThat(item1).isNotEqualTo(item4);
        assertThat(item1).isNotEqualTo(item5);
        assertThat(item1).isNotEqualTo(item6);
        assertThat(item1).isNotEqualTo(item7);
        assertThat(item1).isNotEqualTo(null);
        assertThat(item1).isNotEqualTo(new Object());
        assertThat(item1).isNotEqualTo("");
    }

    @Test
    @DisplayName("测试equals方法null值处理")
    void testEqualsMethodWithNulls() {
        PantryItem item1 = new PantryItem(1, 100, 200, null, futureDate);
        PantryItem item2 = new PantryItem(1, 100, 200, null, futureDate);
        PantryItem item3 = new PantryItem(1, 100, 200, "1kg", futureDate);

        assertThat(item1).isEqualTo(item2);
        assertThat(item1).isNotEqualTo(item3);

        PantryItem item4 = new PantryItem(1, 100, 200, "1kg", null);
        PantryItem item5 = new PantryItem(1, 100, 200, "1kg", null);
        PantryItem item6 = new PantryItem(1, 100, 200, "1kg", futureDate);

        assertThat(item4).isEqualTo(item5);
        assertThat(item4).isNotEqualTo(item6);

        PantryItem item7 = new PantryItem(1, 100, 200, null, null);
        PantryItem item8 = new PantryItem(1, 100, 200, null, null);

        assertThat(item7).isEqualTo(item8);
    }

    @Test
    @DisplayName("测试hashCode一致性")
    void testHashCodeConsistency() {
        PantryItem item1 = new PantryItem(1, 100, 200, "1kg", futureDate);
        PantryItem item2 = new PantryItem(1, 100, 200, "1kg", futureDate);

        int hash1 = item1.hashCode();
        int hash2 = item2.hashCode();

        assertThat(item1).isEqualTo(item2);
        assertThat(hash1).isEqualTo(hash2);

        item1.setQuantity("2kg");
        int hash3 = item1.hashCode();
        assertThat(hash3).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("测试hashCode各字段影响")
    void testHashCodeFieldEffect() {
        PantryItem baseItem = new PantryItem(1, 100, 200, "1kg", futureDate);
        int baseHash = baseItem.hashCode();

        PantryItem itemWithDiffId = new PantryItem(2, 100, 200, "1kg", futureDate);
        assertThat(itemWithDiffId.hashCode()).isNotEqualTo(baseHash);

        PantryItem itemWithDiffUserId = new PantryItem(1, 101, 200, "1kg", futureDate);
        assertThat(itemWithDiffUserId.hashCode()).isNotEqualTo(baseHash);

        PantryItem itemWithDiffIngredientId = new PantryItem(1, 100, 201, "1kg", futureDate);
        assertThat(itemWithDiffIngredientId.hashCode()).isNotEqualTo(baseHash);

        PantryItem itemWithDiffQuantity = new PantryItem(1, 100, 200, "2kg", futureDate);
        assertThat(itemWithDiffQuantity.hashCode()).isNotEqualTo(baseHash);

        PantryItem itemWithDiffDate = new PantryItem(1, 100, 200, "1kg", pastDate);
        assertThat(itemWithDiffDate.hashCode()).isNotEqualTo(baseHash);
    }

    @Test
    @DisplayName("测试hashCode与null字段")
    void testHashCodeWithNullFields() {
        PantryItem itemWithNulls = new PantryItem(1, 100, 200, null, null);
        int hash = itemWithNulls.hashCode();
        assertThat(hash).isNotZero();

        // 再次计算hash应一致
        int sameHash = itemWithNulls.hashCode();
        assertThat(sameHash).isEqualTo(hash);
    }
}
