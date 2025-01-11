package dev.minechase.core.api.report.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.report.model.Report;
import dev.minechase.core.api.packet.ServerResponsePacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReportUpdatePacket extends ServerResponsePacket {

    private final Report report;
    private final String executeServer;

    public ReportUpdatePacket(Report report) {
        this.report = report;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getReportHandler().updateReport(this.report);
        CoreAPI.getInstance().getReportHandler().saveReport(this.report, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getReportHandler().updateReport(this.report);
    }

}
