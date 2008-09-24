      <div id="sidebar">
        <fieldset id="status">
          <legend>$tr.label("status")</legend>
          <ul>
            <li class="uptime">$tr.label("uptime") : $UPTIME</li>
            <li class="queue">$tr.label("queue_length") : $scheduler.WaitingJobs</li>
          </ul>
          <table>
            <tr>
              <td style="width: 50%; padding: 0;">
                <form id="start" method="post" action="restart">
                  <input type="submit" style="width: 100%;" value="$tr.label("restart")" />
                </form>
              </td>
              <td style="width: 50%; padding: 0;">
                <form id="stop" method="post" action="stop">
                  <input type="submit" style="width: 100%;" value="$tr.label("stop")" />
                </form>
              </td>
            </tr>
          </table>
        </fieldset>
        <fieldset id="jobs">
          <legend>$tr.label("job_info")</legend>
          <iframe style="width: 100%;" frameborder="0" scrolling="no" src="/jobstat">
#parse("jobstat.inc")
          </iframe>
        </fieldset>
        <fieldset id="options">
          <legend>$tr.label("options")</legend>
          <form id="motd" method="post" action="motd">
            <table>
              <tr>
                <td>$tr.label("motd") :</td>
              </tr>
              <tr>
                <td>
                  <input type="text" style="width: 99%;" class="form" name="motdtext"/>
                </td>
              </tr>
              <tr>
                <td style="text-align: right;">
                  <input type="submit" style="width: 100px;" value="$tr.label("set_save")" />
                </td>
              </tr>
            </table>
          </form>
        </fieldset>
      </div>
      <div id="footer">
        <p>
        $COPYRIGHT
        </p>
      </div>
    </div>
  </body>
</html>
