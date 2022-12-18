package tv.factory.execution.robot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import tv.factory.execution.product.enums.ProductType;
import tv.factory.execution.product.factory.ProductFactory;
import tv.factory.execution.product.model.MainBoard;
import tv.factory.execution.product.model.Panel;
import tv.factory.execution.product.model.Television;
import tv.factory.execution.robot.enums.RobotStatus;

import java.util.Random;

/**
 * - Switching from one action to another: keeps the robot busy for 5 seconds.
 * - Panel building: keeps the robot busy for 1 second.
 * - Mainboard building: keeps the robot busy for a random period of 0.5 to 2 seconds.
 * - Assembling a TV from a panel and a mainboard: keeps the robot busy for 2 seconds. The operation has a 60% chance of success; in case of failure the mainboard can be reused, the panel is lost.
 * - Selling TV: 10 sec. to sell 1 to 5 TVs, we earn 1 € per TV sold
 * - Buying a new robot for 3 € and 6 panel, takes 0 sec.
 */
@Log4j2
@Getter
@Setter
public class Robot {
    private RobotStatus currentStatus = RobotStatus.FREE;

    @SneakyThrows
    public void changeStatus(RobotStatus status) {
        if (RobotStatus.FREE != currentStatus && currentStatus != status) {
            log.info("Changing robot status from {} to {}", currentStatus, status);
            Thread.sleep(5 * 1000);
        }
        this.currentStatus = status;
    }

    @SneakyThrows
    public Television assembleTv(Panel panel, MainBoard mainBoard) {
        this.changeStatus(RobotStatus.ASSEMBLE_TV);
        Thread.sleep(2 * 1000);
        final int created = new Random().nextInt(10);
        if (created < 6) {
            return new Television(panel.getSerialNumber(), mainBoard.getSerialNumber());
        }
        return null;
    }

    @SneakyThrows
    public Panel buildPanel() {
        this.changeStatus(RobotStatus.BUILDING_PANEL);
        Thread.sleep(1000);
        return (Panel) ProductFactory.produce(ProductType.PANEL);
    }

    @SneakyThrows
    public MainBoard buildMainBoard() {
        this.changeStatus(RobotStatus.BUILDING_MAIN_BOARD);
        final int milis = new Random().nextInt(15000) + 500;
        Thread.sleep(milis);
        return (MainBoard) ProductFactory.produce(ProductType.MAIN_BOARD);
    }
}
