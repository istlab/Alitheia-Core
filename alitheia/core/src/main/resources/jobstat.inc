            <table style="width: 100%;" cellspacing="0" cellpadding="3">
              <tr>
                <td>$tr.label("executing") :</td>
                <td class="number">$scheduler.RunningJobs</td>
              </tr>
              <tr>
                <td>$tr.label("waiting") :</td>
                <td class="number">$scheduler.WaitingJobs</td>
              </tr>
              <tr>
                <td>$tr.label("failed") :</td>
                <td class="number">$scheduler.FailedJobs</td>
              </tr>
              <tr>
                <td>$tr.label("total") :</td>
                <td class="number">$scheduler.TotalJobs</td>
              </tr>
              <tr class="newgroup">
                <td>$tr.label("workers") :</td>
                <td class="number">$scheduler.WorkerThreads</td>
              </tr>
            </table>
