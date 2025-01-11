package dev.minechase.core.api.report.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.report.model.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReportDeletePacket extends ServerResponsePacket {

    private final Report report;
    private final String executeServer;

    public ReportDeletePacket(Report report) {
        this.report = report;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getReportHandler().removeReport(this.report);
        CoreAPI.getInstance().getReportHandler().deleteReport(this.report, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getReportHandler().removeReport(this.report);
    }

}
