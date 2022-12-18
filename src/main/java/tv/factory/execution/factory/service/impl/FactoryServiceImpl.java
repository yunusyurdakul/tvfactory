package tv.factory.execution.factory.service.impl;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tv.factory.execution.factory.model.FactoryResultDto;
import tv.factory.execution.factory.service.FactoryService;
import tv.factory.execution.product.enums.ProductType;
import tv.factory.execution.product.factory.ProductFactory;
import tv.factory.execution.product.model.MainBoard;
import tv.factory.execution.product.model.Panel;
import tv.factory.execution.product.model.Television;
import tv.factory.execution.robot.enums.RobotStatus;
import tv.factory.execution.robot.model.Robot;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Log4j2
public class FactoryServiceImpl implements FactoryService {
    private AtomicInteger balance = new AtomicInteger(0);
    private AtomicInteger robotCount = new AtomicInteger(2);
    private AtomicInteger totalTvCount = new AtomicInteger(0);
    private Queue<Robot> robots = new ConcurrentLinkedQueue<>();
    private Queue<Panel> panels = new ConcurrentLinkedQueue<>();
    private Queue<MainBoard> mainBoards = new ConcurrentLinkedQueue<>();
    private Queue<Television> televisions = new ConcurrentLinkedQueue<>();

    @Override
    public FactoryResultDto startProduction() {
        this.initFactory();
        final ForkJoinPool productionThreadPool = (ForkJoinPool) Executors.newWorkStealingPool(30);
        while (robotCount.get() < 30) {
            final Robot robot = robots.poll();
            if (Objects.nonNull(robot)) {
                productionThreadPool.submit(() -> {
                    if (televisions.size() >= 5) {
                        this.sellTv(robot);
                    }
                    this.assembleTv(robot);
                    this.producePanels(robot);
                    this.produceMainBoard(robot);
                    this.buyNewRobot(robot);
                    robots.add(robot);
                });
            }
        }
        productionThreadPool.shutdownNow();
        return FactoryResultDto.builder()
                .totalTvCount(totalTvCount.get())
                .finalBalance(balance.get())
                .robotCount(robotCount.get())
                .build();
    }

    private void produceMainBoard(Robot robot) {
        mainBoards.add(robot.buildMainBoard());
        log.info("A main board is produced");
    }

    private void producePanels(Robot robot) {
        // at this point production will be faster
        if (robotCount.get() > 5 && balance.get() >= 3) {
            buySixNewPanel(robot);
        } else {
            panels.add(robot.buildPanel());
            log.info("A panel is produced");
        }
    }

    private void assembleTv(Robot robot) {
        if (panels.size() > 0 && mainBoards.size() > 0) {
            final MainBoard mainBoard = mainBoards.poll();
            final Television television = robot.assembleTv(panels.poll(), mainBoard);
            if (Objects.nonNull(television)) {
                televisions.add(television);
                totalTvCount.getAndAdd(1);
                log.info("A TV is assembled");
            } else {
                log.info("TV is not assembled, the main board is returned.");
                mainBoards.add(mainBoard);
            }
        }
    }

    private void initFactory() {
        // starts with 2 robots
        robots.add(new Robot());
        robots.add(new Robot());
    }


    @SneakyThrows
    private void sellTv(Robot robot) {
        int tvCount = 0;
        while (tvCount < 5 && Objects.nonNull(televisions.poll())) {
            tvCount++;
        }
        if (tvCount > 0) {
            robot.changeStatus(RobotStatus.SELL_TV);
            Thread.sleep(10 * 1000);
            final int currentBalance = balance.getAndAdd(tvCount);
            log.info("Sold {} televisions", tvCount);
            log.info("Current balance is {}", currentBalance);
        }
    }

    private void buyNewRobot(Robot robot) {
        if (balance.get() >= 3) {
            robot.changeStatus(RobotStatus.BUY_ROBOT);
        }
        // balance may change to this time
        if (balance.get() >= 3) {
            final int currentBalance = balance.getAndAdd(-3);
            robotCount.getAndAdd(1);
            robots.add(new Robot());
            log.info("Bought a new robot. Total robot count is {}", robotCount.get());
            log.info("Current balance is {}", currentBalance);
        }
    }

    private void buySixNewPanel(Robot robot) {
        if (balance.get() >= 3) {
            robot.changeStatus(RobotStatus.BUY_PANEL);
        }
        // balance may change to this time
        if (balance.get() >= 3) {
            final int currentBalance = balance.getAndAdd(-3);
            panels.addAll(List.of(
                    (Panel) ProductFactory.produce(ProductType.PANEL),
                    (Panel) ProductFactory.produce(ProductType.PANEL),
                    (Panel) ProductFactory.produce(ProductType.PANEL),
                    (Panel) ProductFactory.produce(ProductType.PANEL),
                    (Panel) ProductFactory.produce(ProductType.PANEL),
                    (Panel) ProductFactory.produce(ProductType.PANEL)
            ));
            log.info("6 new panel is bought.");
            log.info("Current balance is {}", currentBalance);
        }
    }
}
