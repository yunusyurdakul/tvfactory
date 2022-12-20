package tv.factory.execution.factory.service.impl;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tv.factory.execution.base.model.BaseResponse;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Log4j2
public class FactoryServiceImpl implements FactoryService {
    private AtomicInteger balance = new AtomicInteger(0);
    private AtomicInteger robotCount = new AtomicInteger(2);
    private Queue<Robot> robots = new ConcurrentLinkedQueue<>();
    private Queue<Panel> panels = new ConcurrentLinkedQueue<>();
    private Queue<MainBoard> mainBoards = new ConcurrentLinkedQueue<>();
    private Queue<Television> televisions = new ConcurrentLinkedQueue<>();
    private AtomicBoolean isStarted = new AtomicBoolean(false);

    @Override
    public void startProduction() {
        if (isStarted.get()) {
            return;
        }
        this.initFactory();
        isStarted.set(true);
        // 30 thread for max 30 robots
        final ForkJoinPool productionThreadPool = (ForkJoinPool) Executors.newWorkStealingPool(30);
        while (robotCount.get() < 30) {
            // pop robot
            final Robot robot = robots.poll();
            if (Objects.nonNull(robot)) {
                productionThreadPool.submit(() -> {
                    // not to sell one-by-one
                    // try to roll up tvs
                    if (televisions.size() >= 5) {
                        this.sellTv(robot);
                    }
                    // all robots try these actions in order.
                    // when new robots are added the queue then they will make a circular run.
                    this.assembleTv(robot);
                    if (panels.size() < 5) {
                        this.producePanels(robot);
                    }
                    if (mainBoards.size() < 5) {
                        this.produceMainBoard(robot);
                    }
                    this.buyNewRobot(robot);
                    // queue robot
                    robots.add(robot);
                });
            }
        }
        isStarted.set(false);
        productionThreadPool.shutdownNow();
    }

    @Override
    public BaseResponse result() {
        String message = "Factory did not started!";
        if (robotCount.get() >= 30) {
            message = "Factory reached its goal!";
        } else if (isStarted.get()) {
            message = "Factory is still running!";
        }
        return BaseResponse.response(FactoryResultDto.builder()
                .televisionCount(televisions.size())
                .balance(balance.get())
                .robotCount(robotCount.get())
                .panelCount(panels.size())
                .mainBoardCount(mainBoards.size())
                .build(), message);
    }

    @Override
    public BaseResponse reset() {
        if (isStarted.get()) {
            return BaseResponse.response("Factory is still running, cannot reset!");
        }
        this.initFactory();
        return BaseResponse.response("Factory restore to base settings!");
    }

    private void produceMainBoard(Robot robot) {
        mainBoards.add(robot.buildMainBoard());
        log.info("A main board is produced");
    }

    private void producePanels(Robot robot) {
        // at this point production will be faster
        if (robotCount.get() > 5 && balance.get() >= 3) {
            this.buySixNewPanel(robot);
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
                log.info("A TV is assembled");
            } else {
                log.info("TV did not assembled, the main board is returned.");
                mainBoards.add(mainBoard);
            }
        }
    }

    private void initFactory() {
        balance = new AtomicInteger(0);
        robotCount = new AtomicInteger(2);
        robots = new ConcurrentLinkedQueue<>();
        panels = new ConcurrentLinkedQueue<>();
        mainBoards = new ConcurrentLinkedQueue<>();
        televisions = new ConcurrentLinkedQueue<>();
        isStarted = new AtomicBoolean(false);
        // starts with 2 robots
        robots.addAll(List.of(
                new Robot(),
                new Robot()
        ));
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
            final int currentBalance = balance.addAndGet(tvCount);
            log.info("Sold {} televisions", tvCount);
            log.info("Current balance is {}", currentBalance);
        }
    }

    private void buyNewRobot(Robot robot) {
        if (balance.get() >= 3) {
            final int currentBalance = balance.addAndGet(-3);
            robot.changeStatus(RobotStatus.BUY_ROBOT);
            int currentRobotCount = robotCount.incrementAndGet();
            robots.add(new Robot());
            log.info("Bought a new robot. Total robot count is {}", currentRobotCount);
            log.info("Current balance is {}", currentBalance);
        }
    }

    private void buySixNewPanel(Robot robot) {
        if (balance.get() >= 3) {
            final int currentBalance = balance.addAndGet(-3);
            robot.changeStatus(RobotStatus.BUY_PANEL);
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
