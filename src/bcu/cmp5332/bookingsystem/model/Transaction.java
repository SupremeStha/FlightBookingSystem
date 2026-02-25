package bcu.cmp5332.bookingsystem.model;


import java.util.ArrayList;
import java.util.List;
 /**
 * Transaction class to manage rollback on failure.
 * Provides atomic operations with automatic rollback if any step fails.
 * Usage:
 * <pre>
 * Transaction tx = new Transaction();
 * try {
 *     tx.execute(() -> flight.addPassenger(customer), 
 *                () -> flight.removePassenger(customer));
 *     tx.execute(() -> customer.addBooking(booking), 
*                () -> customer.removeBooking(booking));
*     tx.commit();
* } catch (Exception e) {
*     tx.rollback();
*     throw e;
* }
* </pre>
*/

public class Transaction {
    
    private final List<Runnable> rollbackActions = new ArrayList<>();
    private boolean committed = false;
    private boolean rolledBack = false;
    
    /**
    * Execute an action with its corresponding rollback action.
    * The rollback action will be executed in reverse order if rollback is called.
    *
    * @param action The forward action to execute
    * @param rollback The rollback action to execute if transaction fails
    */

    public void execute(Runnable action, Runnable rollback) {
        if (committed) {
            throw new IllegalStateException("Cannot execute on committed transaction");
        }
        if (rolledBack) {
            throw new IllegalStateException("Cannot execute on rolled back transaction");
        }
        
        // Execute the forward action
        action.run();
        
        // Store rollback action at the beginning (LIFO order)
        rollbackActions.addFirst(rollback);
    }
    
    /**
    * Execute an action without rollback capability.
    * Use this for read-only operations or when rollback is not needed.
    *
    * @param action The action to execute
    */

    public void execute(Runnable action) {
        if (committed) {
            throw new IllegalStateException("Cannot execute on committed transaction");
        }
        if (rolledBack) {
            throw new IllegalStateException("Cannot execute on rolled back transaction");
        }
        
        action.run();
    }
    
    /**
    * Commit the transaction. After commit, no rollback is possible.
    */

    public void commit() {
        if (committed) {
            throw new IllegalStateException("Transaction already committed");
        }
        if (rolledBack) {
            throw new IllegalStateException("Cannot commit rolled back transaction");
        }
        
        committed = true;
        rollbackActions.clear(); // Clear rollback actions as they're no longer needed
    }
    
    /**
    * Rollback all executed actions in reverse order.
    * This should be called in catch blocks when an error occurs.
    */

    public void rollback() {
        if (committed) {
            throw new IllegalStateException("Cannot rollback committed transaction");
        }
        if (rolledBack) {
            return; // Already rolled back, safe to call multiple times
        }
        
        rolledBack = true;
        
        // Execute rollback actions in reverse order (LIFO)
        for (Runnable rollback : rollbackActions) {
            try {
                rollback.run();
            } catch (Exception e) {
                // Log but don't throw - we want to continue rolling back other actions
                System.err.println("Warning: Rollback action failed: " + e.getMessage());
            }
        }
        
        rollbackActions.clear();
    }
    
    /**
    * Check if the transaction has been committed.
    */

    public boolean isCommitted() {
        return committed;
    }
    
    /**
    * Check if the transaction has been rolled back.
    */

    public boolean isRolledBack() {
        return rolledBack;
    }
    
    /**
    * Get the number of pending rollback actions.
    * Useful for debugging.
    */

    public int getPendingActionsCount() {
        return rollbackActions.size();
    }
}
