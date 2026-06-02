package Tg.OSEOR.DIWA.Backend.dto;

import java.util.Map;

public class DashboardStatsDTO {
    private Double totalRevenue;
    private long pendingOrders;
    private long activeAppointments;
    private long pendingSAV;
    private long lowStockCount;
    private Map<String, Long> statusDistribution;
    private java.util.List<Double> weeklyPerformance;
    private java.util.List<ActivityDTO> recentActivities;

    public DashboardStatsDTO() {}

    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }

    public long getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }

    public long getActiveAppointments() { return activeAppointments; }
    public void setActiveAppointments(long activeAppointments) { this.activeAppointments = activeAppointments; }

    public long getPendingSAV() { return pendingSAV; }
    public void setPendingSAV(long pendingSAV) { this.pendingSAV = pendingSAV; }

    public long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; }

    public Map<String, Long> getStatusDistribution() { return statusDistribution; }
    public void setStatusDistribution(Map<String, Long> statusDistribution) { this.statusDistribution = statusDistribution; }

    public java.util.List<Double> getWeeklyPerformance() { return weeklyPerformance; }
    public void setWeeklyPerformance(java.util.List<Double> weeklyPerformance) { this.weeklyPerformance = weeklyPerformance; }

    public java.util.List<ActivityDTO> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(java.util.List<ActivityDTO> recentActivities) { this.recentActivities = recentActivities; }
}
