package tc.oc.commons.bukkit.experience;

public abstract class ExperienceResult implements Runnable {
    protected boolean success = false;

    public void setSuccess(boolean newValue) {
        this.success = newValue;
        run();
    }

    @Override
    public abstract void run();
}
