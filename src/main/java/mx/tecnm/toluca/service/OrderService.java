package mx.tecnm.toluca.service;

import mx.tecnm.toluca.model.Order;
import mx.tecnm.toluca.repository.OrderRepository;

import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService() {
        this.orderRepository = new OrderRepository();
    }

    public void addOrder(Order order) {
        orderRepository.save(order);
    }

    public List<Order> getOrders(int page, int pageSize) {
        return orderRepository.findAll(page, pageSize);
    }

    public long getOrderCount() {
        return orderRepository.count();
    }

    public void updateOrder(Order order) {
        orderRepository.update(order);
    }
}